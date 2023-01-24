package com.example.blogservice.security.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.blogservice.exception.NotValidTokenException;
import com.example.blogservice.security.token.JwtAuthenticationToken;
import com.example.blogservice.security.user.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private final UserDetailsService userDetailsService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
//    Do nothing
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication){
        final JwtAuthenticationToken authToken = (JwtAuthenticationToken) authentication;
        final String token = authToken.getToken();
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String name = decodedJWT.getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(name);
            return new AuthenticatedUser(userDetails.getUsername(),token,decodedJWT.getClaim("roles").asString());
        }catch (JWTDecodeException e){
            throw new AuthenticationCredentialsNotFoundException(e.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }


}
