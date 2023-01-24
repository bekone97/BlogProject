package com.example.blogservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Id of post which applied application", example = "1", implementation = Long.class)
    private Long id;
    @Schema(description = "Some text are written in this post", implementation = String.class)
    private String content;
    @Schema(description = "User who has written this post", implementation = UserDtoResponse.class)
    private UserDtoResponse userDtoResponse;

    @Schema(description = "Some file in post")
    private LoadFile file;

    private String title;

    @Schema(description = "List of comments which are in this post", implementation = CommentDtoResponse.class)
    private List<CommentDtoResponse> comments;
}
