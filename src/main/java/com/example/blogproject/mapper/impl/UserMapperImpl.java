package com.example.blogproject.mapper.impl;

import com.example.blogproject.dto.UserDto;
import com.example.blogproject.dto.UserDtoRequest;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.mapper.UserMapper;
import com.example.blogproject.model.User;
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
    public User mapToUser(Long userId, UserDtoRequest userDtoRequest) {
        User user = modelMapper.map(userDtoRequest, User.class);
        user.setId(userId);
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
