package com.example.blogservice.changeunit;

import io.mongock.api.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.validation.Validator;

@ChangeUnit(id = "2023-22-01-init-model_update_statistics-collection", order = "006", author = "miachyn.a")
@RequiredArgsConstructor
public class InitModelUpdateStatistics {
    private final MongoTemplate mongoTemplate;

    @BeforeExecution
    public void beforeExecution() {
        mongoTemplate.createCollection("model_update_statistics", CollectionOptions.empty()
                .validator(Validator.schema(MongoJsonSchema.builder()
                        .properties(
                                JsonSchemaProperty.int64("id"),
                                JsonSchemaProperty.string("model_name"),
                                JsonSchemaProperty.object("model_id"),
                                JsonSchemaProperty.objectId("update_count")
                        )
                        .build())));
    }

    @Execution
    public void changeSet() {
    }

    @RollbackBeforeExecution
    public void rollbackBefore() {
        mongoTemplate.dropCollection("model_update_statistics");
    }

    @RollbackExecution
    public void rollback() {
        mongoTemplate.findAllAndRemove(new Query(), "model_update_statistics");
    }
}
