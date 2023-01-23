package com.example.blogservice.mapper.impl;

import com.example.blogservice.dto.UserDto;
import com.example.blogservice.dto.UserDtoRequest;
import com.example.blogservice.dto.UserDtoResponse;
import com.example.blogservice.mapper.UserMapper;
import com.example.blogservice.model.Role;
import com.example.blogservice.model.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {

    private final ModelMapper modelMapper;

    @Override
    public UserDtoResponse mapToUserDtoResponse(User user) {
        return modelMapper.map(user,UserDtoResponse.class);
    }

    @Override
    public User mapToUser(Long userId, UserDtoRequest userDtoRequest, String password, Role role) {
        User user = modelMapper.map(userDtoRequest, User.class);
        user.setId(userId);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }

    @Override
    public User mapToUser(UserDtoResponse userDtoResponse) {
        return modelMapper.map(userDtoResponse,User.class);
    }

    @Override
    public User mapToUser(UserDto userDto) {
        return modelMapper.map(userDto,User.class);
    }

    @Override
    public UserDto mapToUserDto(User user) {
        return modelMapper.map(user,UserDto.class);
    }
}
