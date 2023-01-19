package com.example.blogproject.service.impl;

import com.example.blogproject.dto.UserDto;
import com.example.blogproject.dto.UserDtoRequest;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.exception.ResourceNotFoundException;
import com.example.blogproject.mapper.UserMapper;
import com.example.blogproject.model.Role;
import com.example.blogproject.model.User;
import com.example.blogproject.repository.UserRepository;
import com.example.blogproject.security.user.AuthenticatedUser;
import com.example.blogproject.service.FileService;
import com.example.blogproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
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
        return userRepository.findById(id)
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
        User user = userMapper.mapToUser(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME),userDtoRequest);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setRole(Role.ROLE_USER);
        return userMapper.mapToUserDtoResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDtoResponse update(Long userId, UserDtoRequest userDtoRequest) {
        log.info("Check existing user by userId : {} and update it by :{}",userId,userDtoRequest);
        return userRepository.findById(userId)
                .map(user->{
                    User editedUser = userMapper.mapToUser(userId, userDtoRequest);
                    editedUser.setPassword(user.getPassword());
                    editedUser.setRole(user.getRole());
                    return editedUser;
                })
                .map(userRepository::save)
                .map(userMapper::mapToUserDtoResponse)
                .orElseThrow(()->{
                    log.error("User with id = {} wasn't found", userId);
                    return new ResourceNotFoundException(User.class,"id",userId);
                });
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        log.info("Check existing user by userId : {} and delete id",userId);
        UserDtoResponse user = getById(userId);
        userRepository.deleteById(userId);
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
    public UserDtoResponse changePasswordByUserId(Long id, String password) {
        AuthenticatedUser currentUser = (AuthenticatedUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userRepository.existsByIdAndUsername(id,currentUser.getUsername())||
                currentUser.getAuthorities().stream().anyMatch(authority->authority.getAuthority().equals("ROLE_ADMIN"))){
            userRepository.findById(id)
                    .map(user ->{
                        user.setPassword(password);
                        User save = userRepository.save(user);
                        return userMapper.mapToUserDtoResponse(save);
                    } )
                    .orElseThrow(()->{
                        log.error("There isn't user with id : {}",id);
                        throw new ResourceNotFoundException(User.class,"id",id);
                    });
        }
        throw new RuntimeException();
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
}
