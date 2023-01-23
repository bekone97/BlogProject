package com.example.blogservice.service;

import com.example.blogservice.dto.UserDto;
import com.example.blogservice.model.RefreshToken;

import java.time.LocalDateTime;


public interface RefreshTokenService {
    void deactivateRefreshTokensByUserId(Long id);

    RefreshToken createRefreshToken(UserDto user);

    RefreshToken getByToken(String refreshToken);

    RefreshToken replaceToken(RefreshToken token, UserDto user, LocalDateTime currentDate);

    RefreshToken save(RefreshToken refreshToken);

    RefreshToken update(RefreshToken refreshToken, Long id);
}
