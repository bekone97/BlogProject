package com.example.blogproject.exception;

import java.util.Objects;

import static com.example.blogproject.utils.ConstantUtil.Exception.NO_FOUND_PATTERN;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(Class<?> resourceType, String fieldName, Object fieldValue) {
        super(String.format(NO_FOUND_PATTERN,resourceType.getSimpleName(),fieldName,fieldValue));
    }
}
