package com.example.blogservice.dto;

import com.example.blogservice.validator.UserAgeConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDtoRequest {

    @Schema(description = "User's username",example = "Arteminio",minLength = 6,maxLength = 20)
    @Size(min = 6,message = "{user.validation.username.min}")
    @Size(max = 20,message = "{user.validation.username.max}")
    @NotBlank
    private String username;

    @Schema(description = "User's email", example = "amdsldmal@mail.ru",implementation = String.class)
    @Email(message = "{user.validation.email}")
    private String email;

    @Schema(description = "User's date of birth",example = "2000-01-18",pattern = "yyyy-MM-dd",implementation = LocalDate.class)
    @UserAgeConstraint
    @NotNull(message = "{user.validation.dateOfBirth.notNull}")
    private LocalDate dateOfBirth;
}
