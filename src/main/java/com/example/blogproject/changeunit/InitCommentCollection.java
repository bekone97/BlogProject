package com.example.blogproject.changeunit;

import com.example.blogproject.model.Post;
import com.example.blogproject.model.User;
import com.mongodb.client.model.IndexOptions;
import io.mongock.api.annotations.*;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.validation.Validator;

@ChangeUnit(id="2023-17-01-init-comment-collection", order = "004", author = "miachyn.a")
@RequiredArgsConstructor
public class InitCommentCollection {
    private final MongoTemplate mongoTemplate;

    @BeforeExecution
    public void beforeExecution(){
        mongoTemplate.createCollection("comment", CollectionOptions.empty()
                        .validator(Validator.schema(MongoJsonSchema.builder()
                                        .required("text","user")
                                .properties(
                                        JsonSchemaProperty.int64("id"),
                                        JsonSchemaProperty.string("text"),
                                        JsonSchemaProperty.object("user")
                                )
                                .build())));
    }
    @Execution
    public void changeSet(){
//        Do nothing
    }
    @RollbackBeforeExecution
    public void rollbackBefore(){
        mongoTemplate.dropCollection("comment");
    }

    @RollbackExecution
    public void  rollback(){
//        Do nothing
    }
}
