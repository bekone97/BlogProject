package com.example.blogservice.service;

import com.example.blogservice.dto.UserDto;
import com.example.blogservice.dto.UserDtoRequest;
import com.example.blogservice.dto.UserDtoResponse;
import com.example.blogservice.security.user.AuthenticatedUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDtoResponse getById(Long id);

    UserDto getInnerUserById(Long id);

    Page<UserDtoResponse> findAll(Pageable pageable);

    UserDtoResponse save(UserDtoRequest userDtoRequest, String password);

    UserDtoResponse update(Long userId, UserDtoRequest userDtoRequest, AuthenticatedUser authenticatedUser);

    void deleteById(Long userId, AuthenticatedUser authenticatedUser);

    boolean existsById(Long id);


    UserDto getUserByUsername(String username);

    UserDtoResponse changePasswordByUserId(Long id, String password, AuthenticatedUser authenticatedUser);

}
