package com.example.blogservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "refresh_token")
public class RefreshToken {

    @Id
    private Long refreshTokenId;
    @Transient
    public static final String SEQUENCE_NAME = "refresh_token_sequence";

    private String token;

    private LocalDateTime expires;

    private LocalDateTime created;

    private LocalDateTime revoked;

    private String replacedByToken;

    private boolean isActive;

    @DBRef
    private User user;
}
