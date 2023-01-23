package com.example.blogservice.exception;

import static com.example.blogservice.utils.ConstantUtil.Exception.NO_FOUNDED_FROM_RESOURCE_PATTERN;
import static com.example.blogservice.utils.ConstantUtil.Exception.NO_FOUND_PATTERN;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(Class<?> resourceType, String fieldName, Object fieldValue) {
        super(String.format(NO_FOUND_PATTERN,resourceType.getSimpleName(),fieldName,fieldValue));
    }
    public ResourceNotFoundException(Class<?> resourceType, String fieldName, Object fieldValue,
                                     Class<?> anotherResourceType, String anotherFieldName, Object anotherFieldValue) {
        super(String.format(NO_FOUNDED_FROM_RESOURCE_PATTERN, resourceType.getSimpleName(), fieldName, fieldValue, anotherResourceType.getSimpleName(),
                anotherFieldName, anotherFieldValue));
    }
}
