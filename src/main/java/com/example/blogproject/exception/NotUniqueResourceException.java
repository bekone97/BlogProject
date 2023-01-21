package com.example.blogproject.exception;

public class NotUniqueResourceException extends RuntimeException{

    public NotUniqueResourceException(String message) {
        super(message);
    }

    public NotUniqueResourceException(Class<?> resourceType, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s=%s",
                resourceType.getSimpleName(),fieldName,fieldValue));
    }
}
