package com.example.blogproject.changeunit;

import com.example.blogproject.model.User;
import com.example.blogproject.model.sequence.DatabaseSequence;
import com.example.blogproject.service.impl.SequenceGeneratorService;
import com.mongodb.client.model.IndexOptions;
import io.mongock.api.annotations.*;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.validation.Validator;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@ChangeUnit(id="2023-17-01-init-user-collection", order = "002", author = "miachyn.a")
@RequiredArgsConstructor
public class InitUserCollection {

    private final MongoTemplate mongoTemplate;
    private final List<User> adminList;
    @BeforeExecution
    public void beforeExecution(){
        mongoTemplate.createCollection("user", CollectionOptions.empty()
                .validator(Validator.schema(MongoJsonSchema.builder()
                                .required("username","password","email")
                                .properties(
                                        JsonSchemaProperty.int64("id"),
                                        JsonSchemaProperty.string("username"),
                                        JsonSchemaProperty.string("password"),
                                        JsonSchemaProperty.string("email"),
                                        JsonSchemaProperty.array("posts")
                                )
                        .build())))
                .createIndex(new Document("email",1), new IndexOptions().name("email").unique(true));
}
    @Execution
    public void changeSet(){
        adminList.forEach(user ->
            mongoTemplate.save(user, "user"));
    }
    @RollbackBeforeExecution
    public void rollbackBefore(){
        mongoTemplate.dropCollection("user");
    }

    @RollbackExecution
    public void  rollback(){
        mongoTemplate.findAllAndRemove(new Query(),"user");
    }
}


