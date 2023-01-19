package com.example.blogproject.security.service;

import com.example.blogproject.dto.UserDto;
import com.example.blogproject.model.User;

import java.util.Map;

public interface JWTService {
    Map<String, String> createAccessAndRefreshTokens(UserDto user);

    String getUsernameByTokenHeader(String header);

    Map<String,String> createTokensByRefreshTokenHeader(String header);
}
