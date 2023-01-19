package com.example.blogproject.security.token;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
@Setter
@Getter
public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String token;
    public JwtAuthenticationToken(String token) {
        super(null, null);
        this.token=token;
    }
}
