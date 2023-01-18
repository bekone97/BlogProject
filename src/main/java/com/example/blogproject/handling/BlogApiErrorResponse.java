package com.example.blogproject.handling;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BlogApiErrorResponse {
    private LocalDateTime timestamp = LocalDateTime.now();

    private String message;

    public BlogApiErrorResponse(String message){
        this.message=message;
    }

}
