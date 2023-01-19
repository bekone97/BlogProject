package com.example.blogproject.security.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.blogproject.dto.UserDto;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.model.RefreshToken;
import com.example.blogproject.model.User;
import com.example.blogproject.security.service.JWTService;
import com.example.blogproject.service.RefreshTokenService;
import com.example.blogproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.blogproject.security.filter.JwtAuthenticationTokenFilter.TOKEN_PREFIX;
import static java.lang.System.currentTimeMillis;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JWTServiceImpl implements JWTService {
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private final RefreshTokenService refreshTokenService;
    private final UserService userService;


    @Override
    public String getUsernameByTokenHeader(String header) {
        String token = getValidHeader(header);
        Algorithm algorithm = getAlgorithm();
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token).getSubject();
    }

    @Override
    @Transactional
    public Map<String, String> createAccessAndRefreshTokens(UserDto user) {
        refreshTokenService.deactivateRefreshTokensByUserId(user.getId());
        String accessToken = createAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();
        return createTokensMap(accessToken,refreshToken);
    }


    private String createAccessToken(UserDto user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(currentTimeMillis()+1000*60*1000))
                .withIssuer("blogproject.example")
                .withClaim("userId",user.getId())
                .withClaim("roles",user.getRole().name())
                .sign(getAlgorithm());
    }


    @Override
    @Transactional
    public Map<String, String> createTokensByRefreshTokenHeader(String header) {
        String refreshToken = getValidHeader(header);
        RefreshToken token = refreshTokenService.getByToken(refreshToken);
        UserDto user = userService.getInnerUserById(token.getUser().getId());
        return createTokensByRefreshToken(user,token);
    }

    private Map<String, String> createTokensByRefreshToken(UserDto user, RefreshToken token) {
        Map<String,String> tokens = new HashMap<>();
        tokens.put(ACCESS_TOKEN,createAccessToken(user));
        tokens.put(REFRESH_TOKEN,replacedRefreshToken(user,token).toString());
        return tokens;
    }

    private RefreshToken replacedRefreshToken(UserDto user, RefreshToken token) {
        LocalDateTime currentDate = LocalDateTime.now();
        return refreshTokenService.replaceToken(token,user,currentDate);
    }
    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(SECRET_KEY.getBytes());
    }

    private String getValidHeader(String header) {
        if (header !=null && header.startsWith(TOKEN_PREFIX))
            return header.substring(TOKEN_PREFIX.length());
        throw new RuntimeException("Not valid header");
    }


    private Map<String, String> createTokensMap(String accessToken, String refreshToken) {
        Map<String,String> tokens = new HashMap<>();
        tokens.put(ACCESS_TOKEN,accessToken);
        tokens.put(REFRESH_TOKEN,refreshToken);
        return tokens;
    }
}
