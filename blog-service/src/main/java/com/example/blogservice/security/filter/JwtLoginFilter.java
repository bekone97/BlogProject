package com.example.blogservice.security.filter;

import com.example.blogservice.dto.UserDto;
import com.example.blogservice.security.service.JWTService;
import com.example.blogservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
    public static final String SECURITY_USERNAME_KEY = "username";
    public static final String SECURITY_PASSWORD_KEY = "password";


    private final UserService userService;
    private final JWTService jwtService;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String username = request.getParameter(SECURITY_USERNAME_KEY);
        String password = request.getParameter(SECURITY_PASSWORD_KEY);
        return new UsernamePasswordAuthenticationToken(username, password);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        String username =  authResult.getName();
        UserDto user = userService.getUserByUsername(username);
        Map<String, String> tokens = jwtService.createAccessAndRefreshTokens(user);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
