package com.example.blogservice.handling;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ValidationErrorResponse extends BlogApiErrorResponse{
        private List<ValidationMessage> validationMessages;

    public ValidationErrorResponse(List<ValidationMessage> validationMessages) {
        super("Validation error");
        this.validationMessages = validationMessages;
    }
}
