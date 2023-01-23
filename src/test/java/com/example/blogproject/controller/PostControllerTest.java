package com.example.blogproject.controller;

import com.example.blogproject.config.SecurityConfig;
import com.example.blogproject.dto.*;
import com.example.blogproject.exception.ResourceNotFoundException;
import com.example.blogproject.model.Post;
import com.example.blogproject.model.Role;
import com.example.blogproject.security.provider.JwtAuthenticationProvider;
import com.example.blogproject.security.service.JWTService;
import com.example.blogproject.security.user.AuthenticatedUser;
import com.example.blogproject.service.PostService;
import com.example.blogproject.service.UserService;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static com.example.blogproject.utils.TestUtil.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = PostController.class)
@Import(value = {SecurityConfig.class, JwtAuthenticationProvider.class})
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PostService postService;
    @MockBean
    private JWTService jwtService;
    @MockBean
     private UserService userService;

    UserDtoResponse userDtoResponse;
    AuthenticatedUser authenticatedUser;

    PostDtoRequest postDtoRequest;
    PostDtoResponse postDtoResponse;
    Long postId;
    private org.springframework.security.core.userdetails.User userDetails;
    private String somePassword;
    @BeforeEach
    public void setUp(){
        postId=1L;
        userDtoResponse = UserDtoResponse.builder()
                .username(SUBJECT)
                .email("myachinenergo@mail.ru")
                .id(1L)
                .dateOfBirth(LocalDate.now().minusYears(12))
                .build();
        somePassword="somePassword";
        authenticatedUser= new AuthenticatedUser("someUser","laskdlkd","ROLE_ADMIN");
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority((Role.ROLE_ADMIN.name())));
        userDetails=new org.springframework.security.core.userdetails.User(SUBJECT,somePassword,authorities);
        postDtoRequest = PostDtoRequest.builder()
                .content("someContent")
                .title("Yayaya")
                .userId(1L)
                .build();
        postDtoResponse = PostDtoResponse.builder()
                .id(postId)
                .title("Yayaya")
                .content("someContent")
                .userDtoResponse(userDtoResponse)
                .build();

    }

    @AfterEach
    public void tearDown(){
        userDtoResponse=null;
        postDtoRequest = null;
        postDtoResponse = null;
        authenticatedUser=null;
    }


    @Test
    @SneakyThrows
    void deleteByIdFailIdConstraintWithStaticMock() {
        String expectedMessage = "{general.validation.validId.positive}";
        postId = -1L;
        String timestampFormat = "2000-04-05T11:12:13";
        LocalDateTime timestamp = LocalDateTime.parse(timestampFormat);
        String token = getJwtToken();
        authenticatedUser = new AuthenticatedUser(SUBJECT, token.substring(TOKEN_PREFIX.length()), "ROLE_ADMIN");
        when(userService.loadUserByUsername(SUBJECT))
                .thenReturn(userDetails);
        try (MockedStatic<LocalDateTime> time = Mockito.mockStatic(LocalDateTime.class)) {
            time.when(LocalDateTime::now).thenReturn(timestamp);
            mockMvc.perform(delete("/posts/{postId}", postId)
                            .contentType(APPLICATION_JSON_VALUE)
                            .header(AUTHORIZATION, token))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                    .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)))
                    .andExpect(jsonPath("$['timestamp']", is(timestampFormat)))
                    .andExpect(jsonPath("$['message']", is("Validation error")));

            verify(userService, never()).deleteById(postId, authenticatedUser);

        }
    }


    @Test
    @SneakyThrows
    void findAllPosts() {
        List<PostDtoResponse> postList = List.of(postDtoResponse);
        Pageable pageable = PageRequest.of(0, 3, Sort.by("id"));
        Page<PostDtoResponse> expected = new PageImpl<>(postList,pageable,1);
        when(postService.findAll(pageable)).thenReturn(expected);

        String actual = mockMvc.perform(get("/posts")
                        .param("sort", "id,asc")
                        .param("page", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected),actual);
        verify(postService).findAll(pageable);
    }

    @Test
    @SneakyThrows
    void getPostById() {
        PostDtoResponse expected = postDtoResponse;
        when(postService.getById(1L)).thenReturn(postDtoResponse);

        String actual = mockMvc.perform(get("/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(postDtoResponse),actual);
        verify(postService).getById(1L);
    }

    @Test
    @SneakyThrows
    void getPostByIdFailNoPost() {
        String expectedMessage = "Post wasn't found by id";
        when(postService.getById(postId)).thenThrow(new ResourceNotFoundException(Post.class,"id",postId));

        mockMvc.perform(get("/posts/{postId}", postId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(postService).getById(postId);
    }

    @Test
    @SneakyThrows
    void getPostByIdFailNotValidId() {
        String expectedMessage = "{general.validation.validId.positive}";
        postId=-1L;
        when(postService.getById(postId)).thenThrow(new ResourceNotFoundException(Post.class,"id",postId));

        mockMvc.perform(get("/posts/{postId}", postId))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(postService,never()).getById(postId);
    }

    @Test
    @SneakyThrows
    void save() {
        String token = getJwtToken();
        authenticatedUser = new AuthenticatedUser(SUBJECT,token.substring(TOKEN_PREFIX.length()),"ROLE_ADMIN");
        PostDtoResponse expected = postDtoResponse;
        when(userService.loadUserByUsername(SUBJECT)).thenReturn(userDetails);
        when(postService.save(postDtoRequest,authenticatedUser)).thenReturn(postDtoResponse);

        String actual = mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(postDtoRequest))
                        .header(AUTHORIZATION, token))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected),actual);
        verify(postService).save(postDtoRequest,authenticatedUser);
    }

    @Test
    @SneakyThrows
    void saveFailBodyConstraint() {
        String expectedMessage = "{post.validation.title.notNull}";
        postDtoRequest.setTitle(null);
        String token = getJwtToken();
        authenticatedUser = new AuthenticatedUser(SUBJECT,token.substring(TOKEN_PREFIX.length()),"ROLE_ADMIN");

        when(userService.loadUserByUsername(SUBJECT)).thenReturn(userDetails);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(postDtoRequest))
                        .header(AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(postService,never()).save(postDtoRequest,authenticatedUser);
    }
    @Test
    @SneakyThrows
    void saveFailRequestAuthorizationConstraint() {

        when(userService.loadUserByUsername(SUBJECT)).thenReturn(userDetails);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(postDtoRequest)))
                .andExpect(status().isUnauthorized());

        verify(postService,never()).save(postDtoRequest,authenticatedUser);
    }
    @Test
    @SneakyThrows
    void update() {
        String token = getJwtToken();
        authenticatedUser = new AuthenticatedUser(SUBJECT,token.substring(TOKEN_PREFIX.length()),"ROLE_ADMIN");
        PostDtoResponse expected = postDtoResponse;
        when(userService.loadUserByUsername(SUBJECT)).thenReturn(userDetails);
        when(postService.update(postId,postDtoRequest,authenticatedUser)).thenReturn(postDtoResponse);

        String actual = mockMvc.perform(put("/posts/{postId}",postId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(postDtoRequest))
                        .header(AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected),actual);
        verify(postService).update(postId,postDtoRequest,authenticatedUser);
    }

    @Test
    @SneakyThrows
    void updateFailId() {
        String expectedMessage = "{general.validation.validId.positive}";
        postId=-1L;
        String token = getJwtToken();
        authenticatedUser = new AuthenticatedUser(SUBJECT,token.substring(TOKEN_PREFIX.length()),"ROLE_ADMIN");
        when(userService.loadUserByUsername(SUBJECT)).thenReturn(userDetails);

        mockMvc.perform(put("/posts/{postId}",postId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(postDtoRequest))
                        .header(AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(postService,never()).update(postId,postDtoRequest,authenticatedUser);
    }
    @Test
    @SneakyThrows
    void updateFailBodyConstraint() {
        String expectedMessage = "{post.validation.title.notBlank}";
        postDtoRequest.setTitle("");
        String token = getJwtToken();
        authenticatedUser = new AuthenticatedUser(SUBJECT,token.substring(TOKEN_PREFIX.length()),"ROLE_ADMIN");

        when(userService.loadUserByUsername(SUBJECT)).thenReturn(userDetails);

        mockMvc.perform(put("/posts/{postId}",postId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(postDtoRequest))
                        .header(AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(postService,never()).update(postId,postDtoRequest,authenticatedUser);
    }

    @Test
    @SneakyThrows
    void updateFailRequestAuthorizationConstraint() {

        mockMvc.perform(put("/posts/{postId}",postId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(postDtoRequest)))
                .andExpect(status().isUnauthorized());

        verify(postService,never()).update(postId,postDtoRequest,authenticatedUser);
    }

    @Test
    @SneakyThrows
    void deleteById() {
        postDtoRequest.setTitle("");
        String token = getJwtToken();
        authenticatedUser = new AuthenticatedUser(SUBJECT,token.substring(TOKEN_PREFIX.length()),"ROLE_ADMIN");

        when(userService.loadUserByUsername(SUBJECT)).thenReturn(userDetails);

        mockMvc.perform(delete("/posts/{postId}",postId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(postDtoRequest))
                        .header(AUTHORIZATION, token))
                .andExpect(status().isOk());

        verify(postService).deleteById(postId,authenticatedUser);
    }

}