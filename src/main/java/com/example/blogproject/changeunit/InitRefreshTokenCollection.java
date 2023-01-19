package com.example.blogproject.changeunit;

import com.mongodb.client.model.IndexOptions;
import io.mongock.api.annotations.*;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.validation.Validator;

@ChangeUnit(id="2023-19-01-init-refresh_token-collection",order = "005",author = "myachin.a")
@RequiredArgsConstructor
public class InitRefreshTokenCollection {

    private final MongoTemplate mongoTemplate;

    @BeforeExecution
    public void beforeExecution(){
        mongoTemplate.createCollection("refresh_token", CollectionOptions.empty()
                .validator(Validator.schema(MongoJsonSchema.builder()
//                                .required("created","expires","token","refresh_token_id")
                                .properties(
                                        JsonSchemaProperty.int64("refresh_token_id"),
                                        JsonSchemaProperty.string("token"),
                                        JsonSchemaProperty.date("expires"),
                                        JsonSchemaProperty.date("created"),
                                        JsonSchemaProperty.date("revoked"),
                                        JsonSchemaProperty.string("replaced_by_token"),
                                        JsonSchemaProperty.bool("is_active"),
                                        JsonSchemaProperty.object("user")
                                )
                        .build())));
    }
    @Execution
    public void changeSet(){
//       Do nothing
    }
    @RollbackBeforeExecution
    public void rollbackBefore(){
        mongoTemplate.dropCollection("refresh_token");
    }

    @RollbackExecution
    public void  rollback(){
//       Do nothing
    }
}
