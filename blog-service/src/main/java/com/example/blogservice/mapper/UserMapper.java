package com.example.blogservice.mapper;

import com.example.blogservice.dto.UserDto;
import com.example.blogservice.dto.UserDtoRequest;
import com.example.blogservice.dto.UserDtoResponse;
import com.example.blogservice.model.Role;
import com.example.blogservice.model.User;

public interface UserMapper {
    UserDtoResponse mapToUserDtoResponse(User user);

    User mapToUser(Long userId, UserDtoRequest userDtoRequest, String password, Role role);

    User mapToUser(UserDtoResponse userDtoResponse);

    User mapToUser(UserDto userDto);

    UserDto mapToUserDto(User user);
}
