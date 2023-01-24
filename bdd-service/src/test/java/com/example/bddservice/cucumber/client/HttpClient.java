package com.example.bddservice.cucumber.client;

import io.cucumber.spring.ScenarioScope;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.net.URI;

import static com.example.bddservice.cucumber.utils.TestUtil.createHttpHeaders;
import static com.example.bddservice.cucumber.utils.UrlUtil.SERVER_URL;

@Component
@ScenarioScope
public class HttpClient {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    private ResponseEntity<String> response;

    @SneakyThrows
    public void post(String endpoint, String value, String password, Type type) {
        response = restTemplate.exchange(RequestEntity
                        .post(new URI(SERVER_URL + port + endpoint + "?password=" + password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(value, type),
                String.class);
    }

    @SneakyThrows
    public void get(String endpoint) {
        response = restTemplate.exchange(RequestEntity
                        .get(new URI(SERVER_URL + port + endpoint + "?page=0&size=2"))
                        .headers(createHttpHeaders())
                        .build(),
                String.class);
    }

    @SneakyThrows
    public void getById(String endpoint) {
        response = restTemplate.exchange(RequestEntity
                        .get(new URI(SERVER_URL + port + endpoint))
                        .headers(createHttpHeaders())
                        .build(),
                String.class);
    }

    @SneakyThrows
    public void update(String endpoint, String value, Type type) {
        response = restTemplate.exchange(RequestEntity
                        .put(new URI(SERVER_URL + port + endpoint))
                        .headers(createHttpHeaders())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(value, type),
                String.class);
    }

    @SneakyThrows
    public void delete(String endpoint) {
        response = restTemplate.exchange(RequestEntity
                        .delete(new URI(SERVER_URL + port + endpoint))
                        .headers(createHttpHeaders())
                        .build(),
                String.class);
    }

    public ResponseEntity<String> getResponse() {
        return response;
    }
}

