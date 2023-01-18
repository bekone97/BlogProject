package com.example.blogproject.service;

import com.example.blogproject.dto.UserDtoRequest;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.model.User;

import java.util.List;

public interface UserService {

    UserDtoResponse getById(Long id);

    List<UserDtoResponse> findAll();

    UserDtoResponse save(UserDtoRequest userDtoRequest);

    UserDtoResponse update(Long userId, UserDtoRequest userDtoRequest);

    void deleteById(Long userId);


}
