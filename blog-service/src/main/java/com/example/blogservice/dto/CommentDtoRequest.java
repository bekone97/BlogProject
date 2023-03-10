package com.example.blogservice.dto;

import com.example.blogservice.validator.ValidId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDtoRequest {

    @Schema(description = "Text of comment", implementation = String.class)
    @Size(min = 1, message = "{comment.validation.text.min}")
    @Size(max = 300, message = "{comment.validation.text.max}")
    @NotBlank(message = "{comment.validation.text.notBlank}")
    private String text;

    @Schema(description = "Id of user who has written this comment", example = "1", implementation = Long.class)
    @ValidId
    private Long userId;
}
