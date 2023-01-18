package com.example.blogproject.model.sequence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "database_sequence")
@Builder
public class DatabaseSequence {
    @Id
    private String id;

    private Long seq;
}
