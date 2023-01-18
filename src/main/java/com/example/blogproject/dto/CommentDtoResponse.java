package com.example.blogproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDtoResponse {

    @Schema(description = "Id of comment which applied application",implementation = Long.class,example = "1")
    private Long Id;

    @Schema(description = "Text of comment",implementation = String.class)
    private String text;

    @Schema(description = "User who has written this comment",example = "1",implementation = UserDtoResponse.class)
    private UserDtoResponse userDtoResponse;

}
