package com.example.blogservice.config;

import com.example.blogservice.security.filter.JwtAuthenticationTokenFilter;
import com.example.blogservice.security.filter.JwtLoginFilter;
import com.example.blogservice.security.handling.JwtAuthenticationFailureHandler;
import com.example.blogservice.security.handling.JwtAuthenticationSuccessHandler;
import com.example.blogservice.security.provider.JwtAuthenticationProvider;
import com.example.blogservice.security.service.JWTService;
import com.example.blogservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import java.util.Collections;
import java.util.List;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserService userService;
    private final JWTService jwtService;
    private final ObjectMapper objectMapper;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter(userService,jwtService);
        jwtLoginFilter.setFilterProcessesUrl("/users/authenticate");
        http.csrf()
                .disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests((authorize)->authorize.anyRequest().permitAll())
                .addFilter(jwtLoginFilter)
                .addFilterBefore(jwtAuthenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilterBean(){
        final JwtAuthenticationTokenFilter authenticationTokenFilter =
                new JwtAuthenticationTokenFilter(new OrRequestMatcher(
                        List.of(
                                new AntPathRequestMatcher("/users/**","PUT"),
                                new AntPathRequestMatcher("/users/**","DELETE"),
                                new AntPathRequestMatcher("/posts","POST"),
                                new AntPathRequestMatcher("/posts","PUT"),
                                new AntPathRequestMatcher("/posts","DELETE"),
                                new AntPathRequestMatcher("/posts/**","POST"),
                                new AntPathRequestMatcher("/posts/**","PUT"),
                                new AntPathRequestMatcher("/posts/**","DELETE")
                        )
                ));
        authenticationTokenFilter.setAuthenticationManager(authenticationManager());
        authenticationTokenFilter.setAuthenticationSuccessHandler(new JwtAuthenticationSuccessHandler());
        authenticationTokenFilter.setAuthenticationFailureHandler(new JwtAuthenticationFailureHandler(objectMapper));
        return authenticationTokenFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(jwtAuthenticationProvider));
    }


}
