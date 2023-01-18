package com.example.blogproject.service.impl;

import com.example.blogproject.dto.UserDtoRequest;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.exception.ResourceNotFoundException;
import com.example.blogproject.mapper.UserMapper;
import com.example.blogproject.model.User;
import com.example.blogproject.repository.UserRepository;
import com.example.blogproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SequenceGeneratorService sequenceGeneratorService;

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
    public List<UserDtoResponse> findAll() {
        log.info("Find all users");
        return userRepository.findAll()
                .stream().map(userMapper::mapToUserDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDtoResponse save(UserDtoRequest userDtoRequest) {
        log.info("Save user by :{}",userDtoRequest);
        User user = userMapper.mapToUser(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME),userDtoRequest);
        return userMapper.mapToUserDtoResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDtoResponse update(Long userId, UserDtoRequest userDtoRequest) {
        log.info("Check existing user by userId : {} and update it by :{}",userId,userDtoRequest);
        return userRepository.findById(userId)
                .map(user->userMapper.mapToUser(userId,userDtoRequest))
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
}
