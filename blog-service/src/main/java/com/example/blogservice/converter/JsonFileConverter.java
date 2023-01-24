package com.example.blogservice.converter;

import java.util.List;

public interface JsonFileConverter {
    List readValueForList(String fileLocation,Class<?> tClass);
}
