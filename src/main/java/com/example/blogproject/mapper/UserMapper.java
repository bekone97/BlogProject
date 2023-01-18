package com.example.blogproject.mapper;

import com.example.blogproject.dto.UserDtoRequest;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.model.User;

public interface UserMapper {
     UserDtoResponse mapToUserDtoResponse(User user);

     User mapToUser(Long userId, UserDtoRequest userDtoRequest);

     User mapToUser(UserDtoResponse userDtoResponse);
}
