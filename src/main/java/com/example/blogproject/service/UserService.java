package com.example.blogproject.service;

import com.example.blogproject.dto.UserDto;
import com.example.blogproject.dto.UserDtoRequest;
import com.example.blogproject.dto.UserDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService{

    UserDtoResponse getById(Long id);

    UserDto getInnerUserById(Long id);

    Page<UserDtoResponse> findAll(Pageable pageable);

    UserDtoResponse save(UserDtoRequest userDtoRequest, String password);

    UserDtoResponse update(Long userId, UserDtoRequest userDtoRequest);

    void deleteById(Long userId);

    boolean existsById(Long id);


    UserDto getUserByUsername(String username);

    UserDtoResponse changePasswordByUserId(Long id,String password);

}
