package com.example.blogservice.converter.impl;

import com.example.blogservice.converter.JsonFileConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JsonFileConverterImpl implements JsonFileConverter {

    private final ObjectMapper objectMapper;

    public List<?> readValueForList(String fileLocation, Class<?> tClass) {
        try {
            InputStream inputStream = new FileInputStream(fileLocation);
            return objectMapper.readValue(inputStream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, tClass));
        } catch (IOException e) {
            log.error("Init file wasn't found");
            return List.of();
        }
    }
}
