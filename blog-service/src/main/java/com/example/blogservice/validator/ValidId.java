package com.example.blogservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.lang.annotation.*;

@NotNull(message = "{general.validation.validId.notNull}")
@Positive(message = "{general.validation.validId.positive}")
@Target({ElementType.METHOD,ElementType.FIELD,ElementType.ANNOTATION_TYPE,ElementType.CONSTRUCTOR,ElementType.PARAMETER,ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
public @interface ValidId {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default{};
}
