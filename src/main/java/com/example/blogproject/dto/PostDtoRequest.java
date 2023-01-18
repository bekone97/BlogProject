package com.example.blogproject.dto;

import com.example.blogproject.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class PostDtoRequest {
    private Long id;
    private String content;

    private Long userId;
    private String img;
    private String audio;
    private String video;
    private List<Comment> comments;
}
