package com.example.blogservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

public class UserAgeValidator implements ConstraintValidator<UserAgeConstraint,LocalDate> {
    @Override
    public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext constraintValidatorContext) {
        return Optional.ofNullable(dateOfBirth)
                .map(dob-> Period.between(dob,LocalDate.now()).getYears())
                .map(age->age>=12)
                .orElse(true);
    }
}
