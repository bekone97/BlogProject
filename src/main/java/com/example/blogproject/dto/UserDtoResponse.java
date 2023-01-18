package com.example.blogproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDtoResponse {

    @Schema(description = "User's id, which applied application",example = "1",implementation = Long.class)
    private Long id;

    @Schema(description = "User's username",example = "Arteminio",implementation = String.class)
    private String username;

    @Schema(description = "User's password",example = "1231oskmlkmadlk",implementation = String.class)
    private String password;

    @Schema(description = "User's email", example = "amdsldmal@mail.ru",implementation = String.class)
    private String email;

    @Schema(description = "User's date of birth",example = "2000-01-18",pattern = "yyyy-MM-dd",implementation = LocalDate.class)
    private LocalDate dateOfBirth;
}
