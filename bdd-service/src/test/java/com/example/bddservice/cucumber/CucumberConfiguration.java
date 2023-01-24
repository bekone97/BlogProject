package com.example.bddservice.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.DefaultDataTableCellTransformer;
import io.cucumber.java.DefaultDataTableEntryTransformer;
import io.cucumber.java.DefaultParameterTransformer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;

import java.lang.reflect.Type;
import java.util.List;

@Data
@RequiredArgsConstructor
public class CucumberConfiguration {

    private final ObjectMapper objectMapper;

    @DefaultDataTableCellTransformer
    @DefaultDataTableEntryTransformer
    @DefaultParameterTransformer
    public Object transform(final Object from, final Type type) {
        Object transformValue = objectMapper.convertValue(from, objectMapper.constructType(type));
        return transformValue;
    }

    @SneakyThrows
    public List readValueForList(String from, Class<?> tClass) {
        return objectMapper.readValue(from,
                objectMapper.getTypeFactory().constructCollectionType(List.class, tClass));
    }

    @SneakyThrows
    public Page readValueForPage(String from, Class<?> tClass) {
        return objectMapper.readValue(from,
                objectMapper.getTypeFactory().constructParametricType(RestResponsePage.class, tClass));
    }

    @SneakyThrows
    public <T> T readValueForObject(String from, Class<T> tClass) {
        return objectMapper.readValue(from, tClass);
    }

    @SneakyThrows
    public String getStringValue(Object value) {
        return objectMapper.writeValueAsString(value);
    }
}
