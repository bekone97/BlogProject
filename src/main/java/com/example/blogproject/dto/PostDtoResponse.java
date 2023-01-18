package com.example.blogproject.dto;

import com.example.blogproject.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDtoResponse {
    private Long id;
    private String content;
    private UserDtoResponse userDtoResponse;
    private String img;
    private String audio;
    private String video;
    private List<Comment> comments;
}
