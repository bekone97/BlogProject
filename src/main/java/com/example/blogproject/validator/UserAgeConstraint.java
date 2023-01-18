package com.example.blogproject.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {UserAgeValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserAgeConstraint {

    String message() default "user.validation.dateOfBirth.ageConstraint";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
