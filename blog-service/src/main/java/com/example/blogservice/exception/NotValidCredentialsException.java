package com.example.blogservice.exception;

public class NotValidCredentialsException extends RuntimeException {
    public NotValidCredentialsException(String message) {
        super(message);
    }

}
