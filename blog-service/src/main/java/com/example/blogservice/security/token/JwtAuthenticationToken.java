package com.example.blogservice.security.token;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;

@Setter
@Getter
public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String token;
    private Principal principal;
    public JwtAuthenticationToken(String token) {
        super(null, null);
        this.token=token;
    }
}
