package com.example.blogproject.dto;

import com.example.blogproject.validator.ValidId;
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

    private Long id;

    @Size(min = 1, message = "{comment.validation.text.min}")
    @Size(min = 300, message = "{comment.validation.text.max}")
    private String text;

    @ValidId
    private Long userId;
}
