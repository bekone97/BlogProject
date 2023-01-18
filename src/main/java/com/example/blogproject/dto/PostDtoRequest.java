package com.example.blogproject.dto;

import com.example.blogproject.validator.ValidId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDtoRequest {
    @ValidId
    @Schema(description = "Id of user who has written this post",example = "1",implementation = Long.class)
    private Long userId;

    @Schema(description = "Some text are written in this post",implementation = String.class)
    private String content;

    @Schema(description = "img in post")
    private String img;

    @Schema(description = "Audio in post")
    private String audio;

    @Schema(description = "Video in post")
    private String video;
}
