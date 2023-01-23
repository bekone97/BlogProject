package com.example.blogproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@Document(collection = "post")
@NoArgsConstructor
public class Post {

    @Transient
    public static final String SEQUENCE_NAME = "post_sequence";

    @Id
    private Long id;

    private String title;

    private String content;

    @DBRef
    private User user;

    private ObjectId file;

    @DBRef(lazy = true)
    private List<Comment> comments;
}
