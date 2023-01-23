package com.example.bddservice.cucumber.hooks;

import com.example.bddservice.cucumber.repository.SequenceGeneratorTestRepository;
import com.example.bddservice.cucumber.repository.UserTestRepository;
import com.example.blogservice.model.User;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
public class DatabaseHook {
    private final MongoTemplate mongoTemplate;


    @Before("@users")
    @Transactional
    public void clearDatabaseGenerator(){
        mongoTemplate.findAllAndRemove(new Query(),"user");
        mongoTemplate.findAllAndRemove(new Query(),"database_sequence");
    }

    @After("@users")
    @Transactional
    public void tearDownDatabaseGenerator(){
        mongoTemplate.findAllAndRemove(new Query(),"user");
        mongoTemplate.findAllAndRemove(new Query(),"database_sequence");
    }

    @Given("the database has users")
    public void initUserTable(final List<User> users){
        users.forEach(user -> mongoTemplate.save(user,"user"));
    }

    @And("the database has {int} users")
    public void checkQuantity(int expectedQuantity){
        assertEquals(expectedQuantity,mongoTemplate.count(new Query(),"user"));
    }

}
