package com.example.blogservice.security.service;

import com.example.blogservice.dto.UserDto;

import java.util.Map;

public interface JWTService {
    Map<String, String> createAccessAndRefreshTokens(UserDto user);

    String getUsernameByTokenHeader(String header);

    Map<String, String> createTokensByRefreshTokenHeader(String header);
}
