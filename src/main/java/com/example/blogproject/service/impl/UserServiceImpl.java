package com.example.blogproject.service.impl;

import com.example.blogproject.dto.UserDto;
import com.example.blogproject.dto.UserDtoRequest;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.event.ModelCreatedEvent;
import com.example.blogproject.event.ModelDeletedEvent;
import com.example.blogproject.event.ModelType;
import com.example.blogproject.event.ModelUpdatedEvent;
import com.example.blogproject.exception.NotUniqueResourceException;
import com.example.blogproject.exception.NotValidCredentialsException;
import com.example.blogproject.exception.ResourceNotFoundException;
import com.example.blogproject.mapper.UserMapper;
import com.example.blogproject.model.Role;
import com.example.blogproject.model.User;
import com.example.blogproject.repository.UserRepository;
import com.example.blogproject.security.user.AuthenticatedUser;
import com.example.blogproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;


    @Override
    @Cacheable(value = "user",key = "#id")
    public UserDtoResponse getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::mapToUserDtoResponse)
                .orElseThrow(()->{
                    log.error("User with id = {} wasn't found", id);
                    return new ResourceNotFoundException(User.class,"id",id);
                });
    }

    @Override
    public UserDto getInnerUserById(Long id) {
        return  userRepository.findById(id)
                .map(userMapper::mapToUserDto)
                .orElseThrow(()->{
                    log.error("There is no user with id : {}",id);
                    return new ResourceNotFoundException(User.class,"id",id);
                });
    }

    @Override
    public Page<UserDtoResponse> findAll(Pageable pageable) {
        log.info("Find all users");
        return userRepository.findAll(pageable != null ?
                        pageable :
                        PageRequest.of(1, 3, Sort.by("id"))
                )
                .map((userMapper::mapToUserDtoResponse));
    }

    @Override
    @Transactional
    public UserDtoResponse save(UserDtoRequest userDtoRequest, String password) {
        log.info("Save user by :{}",userDtoRequest);
        checkUniqueUsernameAndEmail(userDtoRequest);

        User user = userMapper.mapToUser(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME),userDtoRequest,
                passwordEncoder.encode(password), Role.ROLE_USER);
        UserDtoResponse userDtoResponse =  userMapper.mapToUserDtoResponse(userRepository.save(user));
        publishSave(userDtoResponse.getId());
        return userDtoResponse;
    }


    @Override
    @Transactional
    @CachePut(value = "user",key = "#userId")
    public UserDtoResponse update(Long userId, UserDtoRequest userDtoRequest, AuthenticatedUser authenticatedUser) {
        log.info("Check existing user by userId : {} and update it by :{}",userId,userDtoRequest);
        checkValidCredentials(userId, authenticatedUser);
        return userRepository.findById(userId)
                .map(user->{
                    checkUniqueUsernameAndEmailForUpdate(user,userDtoRequest);
                    return userMapper.mapToUser(userId, userDtoRequest,user.getPassword(),user.getRole());
                })
                .map(userRepository::save)
                .map(user -> {
                    publishUpdate(userId);
                    return userMapper.mapToUserDtoResponse(user);
                })
                .orElseThrow(()->{
                    log.error("User with id = {} wasn't found", userId);
                    return new ResourceNotFoundException(User.class,"id",userId);
                });
    }


    @Override
    @Transactional
    @CacheEvict(value = "user",key = "#userId")
    public void deleteById(Long userId, AuthenticatedUser authenticatedUser) {
        checkValidCredentials(userId,authenticatedUser);
        log.info("Check existing user by userId : {} and delete id",userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            publishDelete(user);
            userRepository.delete(user);
        }else {
            throw new ResourceNotFoundException(User.class,"id",userId);
        }
    }



    @Override
    public boolean existsById(Long id) {
        log.info("Check existing user by user id : {}",id);
        return userRepository.existsById(id);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .map(userMapper::mapToUserDto)
                .orElseThrow(()->{
                    log.error("There is no user with username : {}",username);
                    return new ResourceNotFoundException(User.class,"username",username);
                });
    }

    @Override
    @CachePut(value = "user",key = "#id")
    public UserDtoResponse changePasswordByUserId(Long id, String password, AuthenticatedUser authenticatedUser) {
        checkValidCredentials(id, authenticatedUser);
        return userRepository.findById(id)
                .map(user ->{
                    user.setPassword(passwordEncoder.encode(password));
                    User save = userRepository.save(user);
                    publishUpdate(user.getId());
                    return userMapper.mapToUserDtoResponse(save);
                } )
                .orElseThrow(()->{
                    log.error("There isn't user with id : {}",id);
                    throw new ResourceNotFoundException(User.class,"id",id);
                });
    }

    private void checkValidCredentials(Long id, AuthenticatedUser authenticatedUser) {
        if (authenticatedUser.getAuthorities().stream().noneMatch(authority->authority.getAuthority().equals("ROLE_ADMIN") ||
                !userRepository.existsByIdAndUsername(id,authenticatedUser.getUsername()))){
            log.error("User with username : {} tried to change data of user with id : {}",
                    authenticatedUser.getUsername(),id);
            throw new NotValidCredentialsException("You have no rights to change this data");
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Load user by username : {}", username);
        UserDto user = getUserByUsername(username);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority((user.getRole().name())));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities
        );
    }


    private void checkUniqueUsernameAndEmail(UserDtoRequest userDtoRequest) {
        if (userRepository.existsByUsernameAndEmail(userDtoRequest.getUsername(),userDtoRequest.getEmail())) {
            log.error("User with username : {} already exists", userDtoRequest.getUsername());
            throw new NotUniqueResourceException(User.class,"username", userDtoRequest.getUsername());
        }
    }
    private void checkUniqueUsernameAndEmailForUpdate(User user,UserDtoRequest userDtoRequest) {
        if (!user.getUsername().equals(userDtoRequest.getUsername()) &&
                userRepository.existsByUsername(user.getUsername())){
            throw new NotUniqueResourceException(User.class,"username", userDtoRequest.getUsername());
        }
            if (!user.getEmail().equals(userDtoRequest.getEmail()) &&
            userRepository.existsByEmail(userDtoRequest.getEmail())){
                throw new NotUniqueResourceException(User.class,"email", userDtoRequest.getEmail());
        }
    }


    private void publishUpdate(Long userId) {
        applicationEventPublisher.publishEvent(ModelUpdatedEvent.builder()
                .modelName(User.class.getName())
                .modelId(userId)
                .build());
    }

    private void publishDelete(User user) {
        applicationEventPublisher.publishEvent(ModelDeletedEvent.builder()
                .model(user)
                .modelType(ModelType.USER)
                .build());
    }
    private void publishSave(Long id) {
        applicationEventPublisher.publishEvent(ModelCreatedEvent.builder()
                .modelId(id)
                .modelName(User.class.getName())
                .build());
    }

}
