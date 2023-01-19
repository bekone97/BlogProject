package com.example.blogproject.security.filter;

import com.example.blogproject.security.token.JwtAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JwtAuthenticationTokenFilter extends AbstractAuthenticationProcessingFilter {

    public static final String TOKEN_PREFIX = "Bearer ";

    public JwtAuthenticationTokenFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    protected JwtAuthenticationTokenFilter(RequestMatcher requiresAuthenticationRequestMatcher,
                                           AuthenticationManager authenticationManager) {
        super(requiresAuthenticationRequestMatcher, authenticationManager);
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
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


}
