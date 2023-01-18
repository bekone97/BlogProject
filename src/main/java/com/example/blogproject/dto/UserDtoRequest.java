package com.example.blogproject.dto;

import com.example.blogproject.validator.UserAgeConstraint;
import jakarta.validation.constraints.Email;
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

    private Long id;

    @Size(min = 6,message = "{user.validation.username.min}")
    @Size(max = 20,message = "{user.validation.username.max}")
    private String username;

    @Size(min = 6, message = "{user.validation.password.min}")
    private String password;

    @Email(message = "{user.validation.password.email}")
    private String email;

    @UserAgeConstraint
    @NotNull
    private LocalDate dateOfBirth;
}
