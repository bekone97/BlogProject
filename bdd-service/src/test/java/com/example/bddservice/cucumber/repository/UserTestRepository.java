package com.example.bddservice.cucumber.repository;

import com.example.blogservice.model.User;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.mongodb.repository.MongoRepository;

@TestComponent
public interface UserTestRepository extends MongoRepository<User, Long> {

}
