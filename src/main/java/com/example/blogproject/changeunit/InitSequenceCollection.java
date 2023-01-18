package com.example.blogproject.changeunit;

import com.example.blogproject.model.User;
import com.example.blogproject.model.sequence.DatabaseSequence;
import io.mongock.api.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.validation.Validator;

import java.util.List;

@ChangeUnit(id="2023-17-01-init-sequence-collection",order = "001",author = "miachyn.a")
@RequiredArgsConstructor
public class InitSequenceCollection {
    private final MongoTemplate mongoTemplate;
    private final List<User> adminList;
    @BeforeExecution
    public void beforeExecution(){
        mongoTemplate.createCollection("database_sequence", CollectionOptions.empty()
                        .validator(Validator.schema(MongoJsonSchema.builder()
                                .properties(
                                        JsonSchemaProperty.string("id"),
                                        JsonSchemaProperty.int64("seq")
                                )
                                .build())));
    }
    @Execution
    public void changeSet(){
        String id = adminList.stream().findFirst().get().SEQUENCE_NAME;
        mongoTemplate.save(new DatabaseSequence(id, (long) adminList.size()));
    }
    @RollbackBeforeExecution
    public void rollbackBefore(){
        mongoTemplate.dropCollection("database_sequence");
    }

    @RollbackExecution
    public void  rollback(){
    mongoTemplate.findAllAndRemove(new Query(),"database_sequence");
    }
}
