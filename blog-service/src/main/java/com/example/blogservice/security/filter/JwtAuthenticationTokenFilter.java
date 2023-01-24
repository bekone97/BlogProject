package com.example.blogservice.security.filter;

import com.example.blogservice.handling.BlogApiErrorResponse;
import com.example.blogservice.security.token.JwtAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class JwtAuthenticationTokenFilter extends AbstractAuthenticationProcessingFilter {

    public static final String TOKEN_PREFIX = "Bearer ";
    private final ObjectMapper objectMapper;
    public JwtAuthenticationTokenFilter(RequestMatcher requiresAuthenticationRequestMatcher, ObjectMapper objectMapper) {
        super(requiresAuthenticationRequestMatcher);
        this.objectMapper = objectMapper;
    }

    protected JwtAuthenticationTokenFilter(RequestMatcher requiresAuthenticationRequestMatcher,
                                           AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
        super(requiresAuthenticationRequestMatcher, authenticationManager);
        this.objectMapper = objectMapper;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
       final String token = getJwtFromRequest(request);
       final JwtAuthenticationToken authToken = new JwtAuthenticationToken(token);
       return getAuthenticationManager().authenticate(authToken);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)){
            throw new AuthenticationCredentialsNotFoundException("No Jwt token found in request headers");
        }
        return authorizationHeader.substring(TOKEN_PREFIX.length());
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request,response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        log.error("Rejected access : {} for url : {}",failed.getMessage(),request.getRequestURL());
        final BlogApiErrorResponse blogApiErrorResponse = new BlogApiErrorResponse(failed.getMessage());

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream().println(objectMapper.writeValueAsString(blogApiErrorResponse));
        response.getOutputStream().flush();
    }
}
