package com.example.blogservice.changeunit;

import io.mongock.api.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.validation.Validator;

@ChangeUnit(id = "2023-17-01-init-post-collection", order = "002", author = "miachyn.a")
@RequiredArgsConstructor
public class InitPostCollection {
    private final MongoTemplate mongoTemplate;

    @BeforeExecution
    public void beforeExecution() {
        mongoTemplate.createCollection("post", CollectionOptions.empty()
                .validator(Validator.schema(MongoJsonSchema.builder()
                        .required("user", "title")
                        .properties(
                                JsonSchemaProperty.int64("id"),
                                JsonSchemaProperty.string("content"),
                                JsonSchemaProperty.string("title"),
                                JsonSchemaProperty.object("user"),
                                JsonSchemaProperty.objectId("file"),
                                JsonSchemaProperty.array("comments")
                        )
                        .build())));
//                .createIndex(new Document("id",1), new IndexOptions().name("id").unique(true));
    }

    @Execution
    public void changeSet() {

    }

    @RollbackBeforeExecution
    public void rollbackBefore() {
        mongoTemplate.dropCollection("post");
    }

    @RollbackExecution
    public void rollback() {
        mongoTemplate.findAllAndRemove(new Query(), "post");
    }
}
