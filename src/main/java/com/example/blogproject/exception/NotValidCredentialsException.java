package com.example.blogproject.exception;

public class NotValidCredentialsException extends RuntimeException{
    public NotValidCredentialsException(String message) {
        super(message);
    }

}
