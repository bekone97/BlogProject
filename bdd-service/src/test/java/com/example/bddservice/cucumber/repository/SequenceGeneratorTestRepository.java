package com.example.bddservice.cucumber.repository;

import com.example.blogservice.model.sequence.DatabaseSequence;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.mongodb.repository.MongoRepository;

@TestComponent
public interface SequenceGeneratorTestRepository extends MongoRepository<DatabaseSequence,String> {
}
