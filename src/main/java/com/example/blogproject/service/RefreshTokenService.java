package com.example.blogproject.service;

import com.example.blogproject.dto.UserDto;
import com.example.blogproject.model.RefreshToken;
import com.example.blogproject.model.User;

import java.sql.Ref;
import java.time.LocalDateTime;


public interface RefreshTokenService {
    void deactivateRefreshTokensByUserId(Long id);

    RefreshToken createRefreshToken(UserDto user);

    RefreshToken getByToken(String refreshToken);

    RefreshToken replaceToken(RefreshToken token, UserDto user, LocalDateTime currentDate);

    RefreshToken save(RefreshToken refreshToken);

    RefreshToken update(RefreshToken refreshToken, Long id);
}
