package com.example.blogproject.dto;

import com.example.blogproject.validator.ValidId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDtoRequest {
    @ValidId
    @Schema(description = "Id of user who has written this post",example = "1",implementation = Long.class)
    private Long userId;

    @NotNull(message = "{post.validation.title.notNull}")
    @NotBlank(message = "{post.validation.title.notBlank}")
    private String title;

    @Schema(description = "Some text are written in this post",implementation = String.class)
    private String content;

    @Schema(description = "Some file(for example video,audio, photo) in post")
    private MultipartFile file;
}
