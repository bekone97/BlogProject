package com.example.blogproject.config;

import com.example.blogproject.model.User;
import io.mongock.runner.springboot.EnableMongock;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMongock
public class Config {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public List<User> adminList(){
        return List.of(User.builder()
                .id(1L)
                .username("admin")
                .password("password")
                .email("myachinenergo@mail.ru")
                .build(),User.builder()
                .id(2L)
                .username("secondUser")
                .password("secondPassword")
                .email("bekone97@mail.ru")
                .build());
    }
}
