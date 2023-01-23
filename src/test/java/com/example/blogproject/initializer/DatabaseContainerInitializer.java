package com.example.blogproject.initializer;

import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.lifecycle.Startables;

import java.util.ArrayList;
import java.util.List;

public class DatabaseContainerInitializer {

    @Container
    public static final GenericContainer mongo=new GenericContainer("mongo:6.0.3")
            .withExposedPorts(27017)
            .waitingFor(Wait.forLogMessage(".*Waiting for connections.*\\n", 1))
            .withEnv("MONGO_INITDB_DATABASE","db")
            .withReuse(true);;


    static {
        if (!mongo.isRunning())
            mongo.start();
    }

    @DynamicPropertySource
    static void mongoProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", mongo::getHost);
        registry.add("spring.data.mongodb.port", mongo::getFirstMappedPort);
        registry.add("spring.data.mongodb.database", () -> "db");
    }

}
