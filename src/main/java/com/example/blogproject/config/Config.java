package com.example.blogproject.config;

import com.example.blogproject.model.Role;
import com.example.blogproject.model.User;
import io.mongock.runner.springboot.EnableMongock;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMongock
@EnableSpringDataWebSupport
public class Config {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public List<User> adminList() {
        return List.of(User.builder()
                        .id(1L)
                        .username("admin")
                        .password("password")
                        .email("myachinenergo@mail.ru")
                        .role(Role.ROLE_ADMIN)
                        .dateOfBirth(LocalDate.now().minusYears(16))
                        .build(),
                User.builder()
                        .id(2L)
                        .username("secondUser")
                        .password("secondPassword")
                        .email("bekone97@mail.ru")
                        .role(Role.ROLE_ADMIN)
                        .dateOfBirth(LocalDate.now().minusYears(15))
                        .build());
    }

    @Bean
    public MessageSource validationMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/validation");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean localValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(validationMessageSource());
        return bean;
    }
}
