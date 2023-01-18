package com.example.blogproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "user")
public class User {
    @Id
    private Long id;

    @Transient
    public static final String SEQUENCE_NAME = "user_sequence";

    private String username;

    private String password;

    private String email;

    private LocalDate dateOfBirth;

}
