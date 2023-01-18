package com.example.blogproject.utils;

public class ConstantUtil {
    public static class Exception{
        public final static String NO_FOUND_PATTERN = "%s wasn't found by %s=%s";
        public final static String NO_FOUNDED_FROM_RESOURCE_PATTERN = "%s wasn't found by %s=%s from %s with %s=%s";
    }

    public static class SwaggerResponse{
        public final static String APPLICATION_JSON = "application/json";
        public static final String RESPONSE_CODE_OK = "200";
        public static final String RESPONSE_CODE_CREATED = "201";
        public static final String RESPONSE_CODE_BAD_REQUEST = "400";
        public static final String RESPONSE_CODE_NOT_FOUNDED = "404";
        public static final String RESPONSE_CODE_INTERNAL_SERVER_ERROR = "500";
        public static final String RESPONSE_DESCRIPTION_OK = "OK";
        public static final String RESPONSE_DESCRIPTION_CREATED = "Created";
        public static final String RESPONSE_DESCRIPTION_BAD_REQUEST = "Bad Request";
        public static final String RESPONSE_DESCRIPTION_NOT_FOUNDED = "Not Founded";
    }
}
