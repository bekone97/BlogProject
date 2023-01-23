package com.example.bddservice.cucumber;

import com.example.bddservice.cucumber.initializer.DatabaseContainerInitializer;
import com.example.blogservice.BlogServiceApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = BlogServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {SpringTestConfig.class})
public class CucumberSpringContextConfiguration extends DatabaseContainerInitializer {
}
