package com.example.bddservice.cucumber.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Date;

import static java.lang.System.currentTimeMillis;

public class TestUtil {
    private static String getJwtToken() {
        return "Bearer " + JWT.create()
                .withSubject("admin")
                .withExpiresAt(new Date(currentTimeMillis() + 10 * 10 * 60 * 1000))
                .withIssuer("com.example")
                .withClaim("id", 2L)
                .withClaim("roles", "ROLE_ADMIN")
                .sign(Algorithm.HMAC256("secret"));
    }
    public static HttpHeaders createHttpHeaders()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization",  getJwtToken());
        return headers;
    }
}
