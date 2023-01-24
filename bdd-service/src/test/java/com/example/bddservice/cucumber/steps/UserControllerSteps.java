package com.example.bddservice.cucumber.steps;

import com.example.bddservice.cucumber.CucumberConfiguration;
import com.example.bddservice.cucumber.client.HttpClient;
import com.example.blogservice.dto.UserDtoRequest;
import com.example.blogservice.dto.UserDtoResponse;
import com.example.blogservice.handling.BlogApiErrorResponse;
import com.example.blogservice.handling.ValidationErrorResponse;
import com.example.blogservice.handling.ValidationMessage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.example.bddservice.cucumber.utils.UrlUtil.USERS;
import static com.example.bddservice.cucumber.utils.UrlUtil.USERS_BY_ID;
import static com.mongodb.assertions.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerSteps {

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private CucumberConfiguration cucumberConfiguration;

    @When("the client calls GET for users")
    public void the_client_calls() {
        httpClient.get(USERS);
    }


    @When("the client calls POST for user with new User and password {string}")
    public void the_client_calls_post_method(String password, final UserDtoRequest userDtoRequest) throws Exception {
        httpClient.post(USERS, cucumberConfiguration.getStringValue(userDtoRequest), password, UserDtoRequest.class);
    }

    @When("the client calls PUT for user with id {int} and an updated user")
    public void the_client_calls_put_with_user(Integer id, final UserDtoRequest userDtoRequest) {
        httpClient.update(USERS_BY_ID + id, cucumberConfiguration.getStringValue(userDtoRequest), UserDtoRequest.class);
    }


    @When("the client calls DELETE for user with id {int}")
    public void the_client_calls_delete(Integer id) {
        httpClient.delete(USERS_BY_ID + id);
    }

    @When("the client calls GET for user with id {int}")
    public void the_client_calls_user_by_id(int id) {
        httpClient.getById(USERS_BY_ID + id);
    }


    @Then("a response returned with status code of {int}")
    public void the_client_receives_status_code_of(Integer responseStatus) {
        var response = httpClient.getResponse().getStatusCode().value();
        assertTrue(responseStatus == httpClient.getResponse().getStatusCode().value());
    }

    @And("the client receives page of {int} users")
    public void the_client_receives_users(int expectedNumber) {
        var actualPage = cucumberConfiguration.readValueForPage(httpClient.getResponse().getBody(), UserDtoResponse.class);
        assertEquals(expectedNumber, actualPage.getContent().size());
    }


    @And("the client receives the same saved user with id {int}")
    public void the_client_receive_user_with_id(int expectedId) {
        var actualUser = cucumberConfiguration.readValueForObject(httpClient.getResponse().getBody(), UserDtoResponse.class);
        assertEquals(expectedId, actualUser.getId());
    }


    @And("the client receives an user with new username {string} and the same id {int}")
    public void the_client_receives_updated_user(String expectedUsername, int expectedId) {
        var actualUser = cucumberConfiguration.readValueForObject(httpClient.getResponse().getBody(), UserDtoResponse.class);
        assertEquals(expectedUsername, actualUser.getUsername());
        assertEquals(expectedId, actualUser.getId());
    }

    @And("the client receives an euser with id {int}")
    public void the_client_receives_user_by_id(int expectedId) {
        var actualUser = cucumberConfiguration.readValueForObject(httpClient.getResponse().getBody(), UserDtoResponse.class);
        assertEquals(expectedId, actualUser.getId());
    }

    @And("the client receives a message")
    public void the_client_receives_error_message(BlogApiErrorResponse expected) {
        var actual = cucumberConfiguration.readValueForObject(httpClient.getResponse().getBody(), BlogApiErrorResponse.class);
        assertEquals(expected.getMessage(), actual.getMessage());
    }

    @And("the client receives a list of validation messages")
    public void the_client_receives_list_of_errors(List<ValidationMessage> expectedMessages) {
        var actual = cucumberConfiguration.readValueForObject(httpClient.getResponse().getBody(), ValidationErrorResponse.class);
        assertThat(actual.getValidationMessages())
                .hasSize(expectedMessages.size())
                .hasSameElementsAs(expectedMessages);
    }
}
