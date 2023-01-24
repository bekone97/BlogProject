package com.example.blogservice.controller;

import com.example.blogservice.config.SecurityConfig;
import com.example.blogservice.dto.CommentDtoRequest;
import com.example.blogservice.dto.CommentDtoResponse;
import com.example.blogservice.dto.UserDtoResponse;
import com.example.blogservice.exception.ResourceNotFoundException;
import com.example.blogservice.model.Comment;
import com.example.blogservice.model.Post;
import com.example.blogservice.model.Role;
import com.example.blogservice.security.provider.JwtAuthenticationProvider;
import com.example.blogservice.security.service.JWTService;
import com.example.blogservice.security.user.AuthenticatedUser;
import com.example.blogservice.service.CommentService;
import com.example.blogservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.blogservice.utils.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class)
@Import(value = {SecurityConfig.class, JwtAuthenticationProvider.class})
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CommentService commentService;
    @MockBean
    private JWTService jwtService;
    @MockBean
    private UserService userService;

    UserDtoResponse userDtoResponse;
    AuthenticatedUser authenticatedUser;

    CommentDtoRequest commentDtoRequest;
    CommentDtoResponse commentDtoResponse;
    Long commentId;
    private org.springframework.security.core.userdetails.User userDetails;
    private String somePassword;
    Long postId;
    @BeforeEach
    public void setUp(){
        somePassword="sadkjadk";
        commentId=1L;
        postId=1L;
        authenticatedUser= new AuthenticatedUser("someUser","laskdlkd","ROLE_ADMIN");
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority((Role.ROLE_ADMIN.name())));
        userDetails=new org.springframework.security.core.userdetails.User(SUBJECT,somePassword,authorities);
        commentDtoRequest = CommentDtoRequest.builder()
                .text("Sometext")
                .userId(1L)
                .build();
        commentDtoResponse = CommentDtoResponse.builder()
                .id(commentId)
                .text("Sometext")
                .userDtoResponse(userDtoResponse)
                .build();

    }

    @AfterEach
    public void tearDown(){
        userDtoResponse=null;
        commentDtoRequest=null;
        commentDtoResponse = null;
        authenticatedUser=null;
    }
    @Test
    @SneakyThrows
    void getCommentByPostIdAndCommentId() {
        CommentDtoResponse expected = commentDtoResponse;
        when(commentService.findCommentByPostIdAndCommentId(postId,commentId)).thenReturn(commentDtoResponse);

        String actual = mockMvc.perform(get("/posts/{postId}/comments/{commentId}", postId, commentId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected),actual);
        verify(commentService).findCommentByPostIdAndCommentId(postId,commentId);
    }

    @Test
    @SneakyThrows
    void getCommentByPostIdAndCommentIdFailCommentId() {
        String expectedMessage = "Comment wasn't found by id=1 from Post with id=1";
        when(commentService.findCommentByPostIdAndCommentId(postId,commentId))
                .thenThrow(new ResourceNotFoundException(Comment.class,"id",commentId,Post.class,"id",postId));

        mockMvc.perform(get("/posts/{postId}/comments/{commentId}", postId, commentId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(commentService).findCommentByPostIdAndCommentId(postId,commentId);
    }

    @Test
    @SneakyThrows
    void getCommentByPostIdAndCommentIdFail() {
        List<CommentDtoResponse> commentDtoResponseList = List.of(commentDtoResponse);
        Pageable pageable = PageRequest.of(0, 3, Sort.by("id"));
        Page<CommentDtoResponse> expected=new PageImpl<>(commentDtoResponseList,pageable,1);
        when(commentService.findAllCommentsByPost(1L,pageable)).thenReturn(expected);

        String actual = mockMvc.perform(get("/posts/1/comments")
                        .param("sort", "id,asc")
                        .param("page", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected),actual);
        verify(commentService).findAllCommentsByPost(1L,pageable);
    }

    @Test
    @SneakyThrows
    void getCommentsByPostId() {
        List<CommentDtoResponse> commentDtoResponseList = List.of(commentDtoResponse);
        Pageable pageable = PageRequest.of(0, 3, Sort.by("id"));
        Page<CommentDtoResponse> expected=new PageImpl<>(commentDtoResponseList,pageable,1);
        when(commentService.findAllCommentsByPost(postId,pageable)).thenReturn(expected);

        String actual = mockMvc.perform(get("/posts/{postId}/comments",postId)
                        .param("sort", "id,asc")
                        .param("page", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected),actual);
        verify(commentService).findAllCommentsByPost(postId,pageable);
    }

    @Test
    @SneakyThrows
    void getCommentsByPostIdFailId() {
        String expectedMessage = "Post wasn't found by id=1";
        Pageable pageable = PageRequest.of(0, 3, Sort.by("id"));

        when(commentService.findAllCommentsByPost(postId,pageable))
                .thenThrow(new ResourceNotFoundException(Post.class,"id",postId));

        mockMvc.perform(get("/posts/1/comments")
                        .param("sort", "id,asc")
                        .param("page", "0")
                        .param("size", "3"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(commentService).findAllCommentsByPost(1L,pageable);
    }

    @Test
    @SneakyThrows
    void getCommentsByPostIdFailNotValidId() {
        String expectedMessage = "{general.validation.validId.positive}";
        postId=-1L;
        Pageable pageable = PageRequest.of(0, 3, Sort.by("id"));

        when(commentService.findAllCommentsByPost(postId,pageable))
                .thenThrow(new ResourceNotFoundException(Post.class,"id",postId));

        mockMvc.perform(get("/posts/{postId}/comments",postId)
                        .param("sort", "id,asc")
                        .param("page", "0")
                        .param("size", "3"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(commentService,never()).findAllCommentsByPost(1L,pageable);
    }

    @Test
    @SneakyThrows
    void saveComment() {
        String token = getJwtToken();
        authenticatedUser=new AuthenticatedUser(SUBJECT,token.substring(TOKEN_PREFIX.length()),"ROLE_ADMIN");
        CommentDtoResponse expected = commentDtoResponse;

        when(userService.loadUserByUsername(SUBJECT)).thenReturn(userDetails);
        when(commentService.save(commentDtoRequest,postId,authenticatedUser)).thenReturn(commentDtoResponse);

        String actual = mockMvc.perform(post("/posts/{postId}/comments",postId)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(commentDtoRequest))
                .header(AUTHORIZATION,token))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected),actual);
        verify(commentService).save(commentDtoRequest,postId,authenticatedUser);
    }

    @Test
    @SneakyThrows
    void saveCommentFailBodyConstraint() {
        String token = getJwtToken();
        String expectedMessage = "{comment.validation.text.notBlank}";
        commentDtoRequest.setText("");
        authenticatedUser=new AuthenticatedUser(SUBJECT,token.substring(TOKEN_PREFIX.length()),"ROLE_ADMIN");

        when(userService.loadUserByUsername(SUBJECT)).thenReturn(userDetails);

        mockMvc.perform(post("/posts/{postId}/comments",postId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(commentDtoRequest))
                        .header(AUTHORIZATION,token))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(commentService,never()).save(commentDtoRequest,postId,authenticatedUser);
    }

    @Test
    @SneakyThrows
    void saveCommentFailAuthorizationConstraint() {

        when(userService.loadUserByUsername(SUBJECT)).thenReturn(userDetails);

        mockMvc.perform(post("/posts/{postId}/comments",postId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(commentDtoRequest)))
                .andExpect(status().isUnauthorized());

        verify(commentService,never()).save(commentDtoRequest,postId,authenticatedUser);
    }

    @Test
    @SneakyThrows
    void updateComment() {
        String token = getJwtToken();
        authenticatedUser=new AuthenticatedUser(SUBJECT,token.substring(TOKEN_PREFIX.length()),"ROLE_ADMIN");
        CommentDtoResponse expected = commentDtoResponse;
        when(userService.loadUserByUsername(SUBJECT)).thenReturn(userDetails);
        when(commentService.update(commentId,postId,commentDtoRequest,authenticatedUser)).thenReturn(commentDtoResponse);

        String actual = mockMvc.perform(put("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(commentDtoRequest))
                        .header(AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected),actual);
        verify(commentService).update(commentId,postId,commentDtoRequest,authenticatedUser);
    }

    @Test
    @SneakyThrows
    void updateCommentFailCommentId(){
        String token = getJwtToken();
        String expectedMessage = "{general.validation.validId.positive}";
        commentId=-1L;
        authenticatedUser=new AuthenticatedUser(SUBJECT,token.substring(TOKEN_PREFIX.length()),"ROLE_ADMIN");
        when(userService.loadUserByUsername(SUBJECT)).thenReturn(userDetails);


        mockMvc.perform(put("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(commentDtoRequest))
                        .header(AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(commentService,never()).update(commentId,postId,commentDtoRequest,authenticatedUser);
    }

    @Test
    @SneakyThrows
    void updateCommentFailBodyConstraint(){
        String token = getJwtToken();
        String expectedMessage = "{comment.validation.text.notBlank}";
        commentDtoRequest.setText("");
        authenticatedUser=new AuthenticatedUser(SUBJECT,token.substring(TOKEN_PREFIX.length()),"ROLE_ADMIN");
        when(userService.loadUserByUsername(SUBJECT)).thenReturn(userDetails);


        mockMvc.perform(put("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(commentDtoRequest))
                        .header(AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(commentService,never()).update(commentId,postId,commentDtoRequest,authenticatedUser);
    }

    @Test
    @SneakyThrows
    void updateCommentFailAuthorizationConstraint(){
        mockMvc.perform(put("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(commentDtoRequest)))
                .andExpect(status().isUnauthorized());

        verify(commentService,never()).update(commentId,postId,commentDtoRequest,authenticatedUser);
    }
    @Test
    @SneakyThrows
    void deleteComment() {
        String token = getJwtToken();
        authenticatedUser=new AuthenticatedUser(SUBJECT,token.substring(TOKEN_PREFIX.length()),"ROLE_ADMIN");
        when(userService.loadUserByUsername(SUBJECT)).thenReturn(userDetails);

        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}",postId,commentId)
                .header(AUTHORIZATION,token))
                .andExpect(status().isOk());

        verify(commentService).delete(commentId,postId,authenticatedUser);
    }
}