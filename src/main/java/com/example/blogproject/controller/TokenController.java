package com.example.blogproject.controller;

import com.example.blogproject.security.service.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/tokens")
public class TokenController {
    private final JWTService jwtService;

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> refreshToken(@RequestHeader(AUTHORIZATION) String authorizationHeader){
        return jwtService.createTokensByRefreshTokenHeader(authorizationHeader);
    }
}
