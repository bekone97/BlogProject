package com.example.blogservice.handling;

import com.example.blogservice.exception.NotValidCredentialsException;
import com.example.blogservice.exception.NotValidTokenException;
import com.example.blogservice.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class BlogApiExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public BlogApiErrorResponse resourceNotFoundExceptionHandler(HttpServletRequest request,
                                                                 ResourceNotFoundException exception) {
        log.error("The {}. There is no object in database : {}.Url of request : {}",
                exception.getClass().getSimpleName(), exception.getMessage(), request.getRequestURL());
        return new BlogApiErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ValidationErrorResponse methodArgumentNotValidExceptionHandler(HttpServletRequest request,
                                                                          MethodArgumentNotValidException exception) {

        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(
                exception.getBindingResult().getAllErrors().stream()
                        .map(FieldError.class::cast)
                        .map(fieldError -> new ValidationMessage(fieldError.getField(), fieldError.getDefaultMessage()))
                        .collect(Collectors.toList())
        );

        log.warn("The {}. Validation messages : {} .Url of request : {}",
                exception.getClass().getSimpleName(), validationErrorResponse.getMessage(), request.getRequestURL());
        return validationErrorResponse;
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ValidationErrorResponse constraintViolationExceptionHandler(HttpServletRequest request,
                                                                       ConstraintViolationException exception) {
        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(
                exception.getConstraintViolations().stream()
                        .map(violation -> new ValidationMessage(violation.getPropertyPath().toString(), violation.getMessage()))
                        .collect(Collectors.toList())
        );
        log.warn("The {}. Validation messages : {} .Url of request : {}",
                exception.getClass().getSimpleName(), validationErrorResponse.getMessage(), request.getRequestURL());

        return validationErrorResponse;
    }

    @ExceptionHandler(value = NotValidCredentialsException.class)
    @ResponseStatus(FORBIDDEN)
    public BlogApiErrorResponse notValidCredentialsExceptionHandler(HttpServletRequest request
            , NotValidCredentialsException exception) {
        log.error("The {}. There is no object in database : {}.Url of request : {}",
                exception.getClass().getSimpleName(), exception.getMessage(), request.getRequestURL());
        return new BlogApiErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(value = NotValidTokenException.class)
    @ResponseStatus(FORBIDDEN)
    public BlogApiErrorResponse notValidTokenExceptionHandler(HttpServletRequest request
            , NotValidCredentialsException exception) {
        log.error("The {}. There is no object in database : {}.Url of request : {}",
                exception.getClass().getSimpleName(), exception.getMessage(), request.getRequestURL());
        return new BlogApiErrorResponse(exception.getMessage());
    }

}
