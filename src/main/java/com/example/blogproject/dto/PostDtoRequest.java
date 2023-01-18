package com.example.blogproject.dto;

import com.example.blogproject.validator.ValidId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDtoRequest {

    private Long id;

    @ValidId
    private Long userId;

    private String img;

    private String audio;

    private String video;
}
