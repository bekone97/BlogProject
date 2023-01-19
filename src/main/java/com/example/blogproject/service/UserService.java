package com.example.blogproject.service;

import com.example.blogproject.dto.UserDtoRequest;
import com.example.blogproject.dto.UserDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserDtoResponse getById(Long id);

    Page<UserDtoResponse> findAll(Pageable pageable);

    UserDtoResponse save(UserDtoRequest userDtoRequest);

    UserDtoResponse update(Long userId, UserDtoRequest userDtoRequest);

    void deleteById(Long userId);

    boolean existsById(Long id);


}
