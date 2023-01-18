package com.example.blogproject.dto;

import com.example.blogproject.model.Comment;
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

    @Schema(description = "Id of post which applied application",example = "1",implementation = Long.class)
    private Long id;
    @Schema(description = "Some text are written in this post",implementation = String.class)
    private String content;
    @Schema(description = "User who has written this post",implementation = UserDtoResponse.class)
    private UserDtoResponse userDtoResponse;

    @Schema(description = "img in post")
    private String img;

    @Schema(description = "Audio in post")
    private String audio;
    @Schema(description = "Video in post")
    private String video;

    @Schema(description = "List of comments which are in this post",implementation = CommentDtoResponse.class)
    private List<CommentDtoResponse> comments;
}
