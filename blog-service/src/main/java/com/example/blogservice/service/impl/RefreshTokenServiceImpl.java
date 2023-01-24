package com.example.blogservice.service.impl;

import com.example.blogservice.dto.UserDto;
import com.example.blogservice.exception.ResourceNotFoundException;
import com.example.blogservice.exception.TokenNotActiveException;
import com.example.blogservice.mapper.UserMapper;
import com.example.blogservice.model.RefreshToken;
import com.example.blogservice.repository.RefreshTokenRepository;
import com.example.blogservice.service.RefreshTokenService;
import com.example.blogservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final SequenceGeneratorService sequenceGeneratorService;

    @Value("${jwt.secret}")
    private String SECRET_KEY;


    @Override
    @Transactional
    public RefreshToken save(RefreshToken refreshToken) {
        log.debug("Save new refresh token : {}",refreshToken);
        refreshToken.setRefreshTokenId(sequenceGeneratorService.generateSequence(RefreshToken.SEQUENCE_NAME));
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public RefreshToken update(RefreshToken refreshToken, Long id) {
        log.debug("Save refresh token : {} with id : {}",refreshToken,id);
        refreshToken.setRefreshTokenId(id);
        return refreshTokenRepository.save(refreshToken);
    }


    @Override
    public void deactivateRefreshTokensByUserId(Long userUd) {
        log.debug("Deactivate old refresh token by user userUd : {}",userUd);
        refreshTokenRepository.findRefreshTokensByUserId(userUd)
                .stream()
                .filter(RefreshToken::isActive)
                .forEach(refreshToken -> {
                    refreshToken.setActive(false);
                    refreshToken.setRevoked(LocalDateTime.now());
                    refreshTokenRepository.save(refreshToken);
                });
    }
    @Transactional
    public void deactivateToken(RefreshToken refreshToken, LocalDateTime currentDate){
        refreshToken.setActive(false);
        refreshToken.setRevoked(currentDate);
        refreshTokenRepository.save(refreshToken);
    }
    @Override
    @Transactional
    public RefreshToken createRefreshToken(UserDto user) {
        log.debug("Create refresh token for user : {}",user.getUsername());
        LocalDateTime currentDate = LocalDateTime.now();
        String token = getRefreshToken(user,currentDate);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(Base64.getEncoder().encodeToString(token.getBytes()))
                .user(userMapper.mapToUser(user))
                .expires(currentDate.plusDays(7))
                .created(currentDate)
                .isActive(true)
                .build();
        return save(refreshToken);
    }

    @Override
    public RefreshToken getByToken(String token) {
        return refreshTokenRepository.findRefreshTokenByToken(token)
                .orElseThrow(()-> new ResourceNotFoundException(RefreshToken.class, "refreshToken", token));
    }


    @Override
    @Transactional
    public RefreshToken replaceToken(RefreshToken oldToken, UserDto user, LocalDateTime currentDate) {
        log.debug("Replacement old token : {} by new token for user : {}",oldToken,user.getUsername());
        checkOldToken(oldToken, user, currentDate);
        deactivateToken(oldToken,currentDate);
        String newToken = getRefreshToken(user,currentDate);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(Base64.getEncoder().encodeToString(newToken.getBytes()))
                .user(userMapper.mapToUser(user))
                .expires(currentDate.plusDays(7))
                .created(currentDate)
                .isActive(true)
                .replacedByToken(oldToken.getToken())
                .build();
        return save(refreshToken);
    }



    private String getRefreshToken(UserDto user, LocalDateTime currentDate) {
        return new StringBuffer(user.getUsername())
                .append(user.getId())
                .append(user.getRole())
                .append(currentDate)
                .append(currentDate.plusDays(7))
                .append(SECRET_KEY)
                .toString();
    }

    private void checkTokenIsActive(RefreshToken token, Long userId) {
        if (token.isActive())
            return;
        deactivateRefreshTokensByUserId(userId);
        throw new TokenNotActiveException();
    }

    private void checkTokenExpires(RefreshToken token, LocalDateTime currentDate, Long userId) {
        if (currentDate.isBefore(token.getExpires()))
            return;
        deactivateRefreshTokensByUserId(userId);
//        throw new TokenExpiredException(RefreshToken.class,"expires", token.getExpires())
        throw new TokenNotActiveException();
    }

    private void checkOldToken(RefreshToken token, UserDto user, LocalDateTime currentDate) {
        checkTokenIsActive(token,user.getId());
        checkTokenExpires(token,currentDate,user.getId());
    }


}
