package com.example.blogservice.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;

import static java.lang.System.currentTimeMillis;

public class TestUtil {

    public static final String SUBJECT = "Artem";
    public static final String TOKEN_PREFIX = "Bearer ";


    public static String getJwtToken() {
        return "Bearer " + JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(currentTimeMillis() + 1000 * 60 * 1000))
                .withIssuer("blogproject.example")
                .withClaim("userId", 1L)
                .withClaim("roles", "ROLE_ADMIN")
                .sign(Algorithm.HMAC256("secret".getBytes()));
    }
}


