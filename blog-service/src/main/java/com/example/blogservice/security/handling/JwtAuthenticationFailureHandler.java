package com.example.blogservice.security.handling;

import com.example.blogservice.handling.BlogApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException{
      log.error("Rejected access : {} for url : {}",exception.getMessage(),request.getRequestURL());
      final BlogApiErrorResponse blogApiErrorResponse = new BlogApiErrorResponse(exception.getMessage());

      response.setContentType(APPLICATION_JSON_VALUE);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getOutputStream().println(objectMapper.writeValueAsString(blogApiErrorResponse));
      response.getOutputStream().flush();
    }
}
