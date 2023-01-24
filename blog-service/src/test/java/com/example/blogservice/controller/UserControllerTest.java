package com.example.blogservice.controller;

import com.example.blogservice.config.SecurityConfig;
import com.example.blogservice.dto.UserDtoRequest;
import com.example.blogservice.dto.UserDtoResponse;
import com.example.blogservice.exception.ResourceNotFoundException;
import com.example.blogservice.model.Role;
import com.example.blogservice.model.User;
import com.example.blogservice.security.provider.JwtAuthenticationProvider;
import com.example.blogservice.security.service.JWTService;
import com.example.blogservice.security.user.AuthenticatedUser;
import com.example.blogservice.service.UserService;
import com.example.blogservice.utils.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.blogservice.utils.TestUtil.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(value = {SecurityConfig.class, JwtAuthenticationProvider.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
    @MockBean
    private JWTService jwtService;

    private UserDtoResponse userDtoResponse;
    private UserDtoRequest userDtoRequest;
    private User user;
    private Long userId;
    private String somePassword;
    private org.springframework.security.core.userdetails.User userDetails;
    private AuthenticatedUser authenticatedUser;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        userDtoResponse = UserDtoResponse.builder()
                .id(userId)
                .username("myachin")
                .dateOfBirth(LocalDate.now().minusYears(13))
                .email("Myachinenergo@mail.ru")
                .build();
        userDtoRequest = UserDtoRequest.builder()
                .username("myachin")
                .dateOfBirth(LocalDate.now().minusYears(13))
                .email("Myachinenergo@mail.ru")
                .build();
        somePassword = "somePassword";
        authenticatedUser = new AuthenticatedUser("someUser", "laskdlkd", "ROLE_ADMIN");
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority((Role.ROLE_ADMIN.name())));
        userDetails = new org.springframework.security.core.userdetails.User(SUBJECT, somePassword, authorities);
    }

    @AfterEach
    public void tearDown() {
        userDtoRequest = null;
        userDtoResponse = null;
    }


    @SneakyThrows
    @Test
    void getAllUsers() {
        List<UserDtoResponse> userList = List.of(userDtoResponse);
        Pageable pageable = PageRequest.of(1, 3, Sort.by("id"));
        Page<UserDtoResponse> expected = new PageImpl<>(userList, pageable, 1);
        when(userService.findAll(pageable)).thenReturn(expected);

        String actual = mockMvc.perform(get("/users")
                        .param("sort", "id,asc")
                        .param("page", "1")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), actual);
        verify(userService).findAll(pageable);
    }

    @Test
    @SneakyThrows
    void deleteByIdFailIdConstraintWithStaticMock() {
        String expectedMessage = "{general.validation.validId.positive}";
        userId = -1L;
        String timestampFormat = "2000-04-05T11:12:13";
        LocalDateTime timestamp = LocalDateTime.parse(timestampFormat);
        String token = getJwtToken();
        authenticatedUser = new AuthenticatedUser(SUBJECT, token.substring(TOKEN_PREFIX.length()), "ROLE_ADMIN");
        when(userService.loadUserByUsername(SUBJECT))
                .thenReturn(userDetails);
        try (MockedStatic<LocalDateTime> time = Mockito.mockStatic(LocalDateTime.class)) {
            time.when(LocalDateTime::now).thenReturn(timestamp);
            mockMvc.perform(delete("/users/{userId}", userId)
                            .contentType(APPLICATION_JSON_VALUE)
                            .header(AUTHORIZATION, token))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                    .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)))
                    .andExpect(jsonPath("$['timestamp']", is(timestampFormat)))
                    .andExpect(jsonPath("$['message']", is("Validation error")));

            verify(userService, never()).deleteById(userId, authenticatedUser);

        }
    }

    @SneakyThrows
    @Test
    void getUserById() {
        UserDtoResponse expected = userDtoResponse;
        when(userService.getById(userId)).thenReturn(userDtoResponse);

        String actual = TestUtil.getUserById(mockMvc, userDtoResponse.getId())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), actual);
        verify(userService).getById(1L);
    }

    @SneakyThrows
    @Test
    void getUserByIdFailNoUser() {
        String expectedMessage = "User wasn't found by id";
        when(userService.getById(userId)).thenThrow(new ResourceNotFoundException(User.class, "id", 1));

        TestUtil.getUserById(mockMvc, userDtoResponse.getId())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(userService).getById(1L);
    }

    @SneakyThrows
    @Test
    void getUserByIdFailWrongUserId() {
        String expectedMessage = "{general.validation.validId.positive}";
        userId = -1L;

        TestUtil.getUserById(mockMvc, userId)
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(userService, never()).getById(userId);
    }


    @Test
    @SneakyThrows
    void save() {
        UserDtoResponse expected = userDtoResponse;
        when(userService.save(userDtoRequest, somePassword)).thenReturn(userDtoResponse);

        String actual = mockMvc.perform(post("/users")
                        .param("password", somePassword)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userDtoRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), actual);
        verify(userService).save(userDtoRequest, somePassword);
    }

    @Test
    @SneakyThrows
    void saveFailPasswordConstraint() {
        String expectedMessage = "User password can't be less than 6 symbols";
        somePassword = "sa";
        mockMvc.perform(post("/users")
                        .param("password", somePassword)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userDtoRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));


        verify(userService, never()).save(userDtoRequest, somePassword);
    }

    @Test
    @SneakyThrows
    void saveFailRequestBodyConstraint() {
        String expectedMessage = "{user.validation.username.min}";
        userDtoRequest.setUsername("sa");
        mockMvc.perform(post("/users")
                        .param("password", "sa")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userDtoRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(userService, never()).save(userDtoRequest, somePassword);
    }

    @Test
    @SneakyThrows
    void update() {
        String token = getJwtToken();
        authenticatedUser = new AuthenticatedUser(SUBJECT, token.substring(TOKEN_PREFIX.length()), "ROLE_ADMIN");
        UserDtoResponse expected = userDtoResponse;
        when(userService.loadUserByUsername(SUBJECT))
                .thenReturn(userDetails);
        when(userService.update(userId, userDtoRequest, authenticatedUser)).thenReturn(expected);

        String actual = mockMvc.perform(put("/users/{userId}", userId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(userDtoRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), actual);
        verify(userService).update(userId, userDtoRequest, authenticatedUser);
    }

    @Test
    @SneakyThrows
    void updateFailIdConstraint() {

        String expectedMessage = "{general.validation.validId.positive}";
        userId = -1L;
        String token = getJwtToken();
        authenticatedUser = new AuthenticatedUser(SUBJECT, token.substring(TOKEN_PREFIX.length()), "ROLE_ADMIN");

        when(userService.loadUserByUsername(SUBJECT))
                .thenReturn(userDetails);

        mockMvc.perform(put("/users/{userId}", userId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(userDtoRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(userService, never()).update(userId, userDtoRequest, authenticatedUser);
    }

    @Test
    @SneakyThrows
    void updateFailRequestBodyConstraint() {
        String expectedMessage = "{user.validation.email}";
        userDtoRequest.setEmail("sa");
        String token = getJwtToken();
        authenticatedUser = new AuthenticatedUser(SUBJECT, token.substring(TOKEN_PREFIX.length()), "ROLE_ADMIN");

        when(userService.loadUserByUsername(SUBJECT))
                .thenReturn(userDetails);

        mockMvc.perform(put("/users/{userId}", userId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(userDtoRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(userService, never()).update(userId, userDtoRequest, authenticatedUser);
    }


    @Test
    @SneakyThrows
    void updateFailRequestAuthorizationConstraint() {
        String expectedMessage = "No Jwt token found in request headers";
        mockMvc.perform(put("/users/{userId}", userId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userDtoRequest)))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).update(userId, userDtoRequest, authenticatedUser);
        verify(userService, never()).loadUserByUsername(SUBJECT);
    }

    @Test
    @SneakyThrows
    void deleteById() {

        String token = getJwtToken();
        authenticatedUser = new AuthenticatedUser(SUBJECT, token.substring(TOKEN_PREFIX.length()), "ROLE_ADMIN");
        when(userService.loadUserByUsername(SUBJECT))
                .thenReturn(userDetails);

        mockMvc.perform(delete("/users/{userId}", userId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, token))
                .andExpect(status().isOk());

        verify(userService).deleteById(userId, authenticatedUser);
    }
}