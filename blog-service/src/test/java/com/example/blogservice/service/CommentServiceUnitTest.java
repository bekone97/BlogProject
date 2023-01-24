package com.example.blogservice.service;

import com.example.blogservice.dto.*;
import com.example.blogservice.event.ModelCreatedEvent;
import com.example.blogservice.event.ModelDeletedEvent;
import com.example.blogservice.event.ModelType;
import com.example.blogservice.event.ModelUpdatedEvent;
import com.example.blogservice.exception.NotValidCredentialsException;
import com.example.blogservice.exception.ResourceNotFoundException;
import com.example.blogservice.mapper.CommentMapper;
import com.example.blogservice.mapper.PostMapper;
import com.example.blogservice.mapper.UserMapper;
import com.example.blogservice.model.Comment;
import com.example.blogservice.model.Post;
import com.example.blogservice.model.Role;
import com.example.blogservice.model.User;
import com.example.blogservice.repository.CommentRepository;
import com.example.blogservice.repository.PostRepository;
import com.example.blogservice.repository.UserRepository;
import com.example.blogservice.security.user.AuthenticatedUser;
import com.example.blogservice.service.impl.CommentServiceImpl;
import com.example.blogservice.service.impl.SequenceGeneratorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CommentServiceUnitTest {
    @InjectMocks
    private CommentServiceImpl commentService;


    private ModelMapper modelMapper = new ModelMapper();
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    @Mock
    private CommentMapper commentMapper;
    @Mock
    private SequenceGeneratorService sequenceGeneratorService;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    User user;
    UserDtoResponse userDtoResponse;
    AuthenticatedUser authenticatedUser;
    Comment comment;
    CommentDtoResponse commentDtoResponse;
    CommentDtoRequest commentDtoRequest;
    Post post;
    PostDtoRequest postDtoRequest;
    PostDtoResponse postDtoResponse;
    ModelCreatedEvent modelCreatedEvent;
    ModelUpdatedEvent modelUpdatedEvent;
    ModelDeletedEvent modelDeletedEvent;
    @BeforeEach
    void setUp() {
        user=User.builder()
                .username("Myachin")
                .password("Artsiom")
                .email("myachinenergo@mail.ru")
                .id(1L)
                .dateOfBirth(LocalDate.now().minusYears(12))
                .role(Role.ROLE_ADMIN)
                .build();
        userDtoResponse = modelMapper.map(user,UserDtoResponse.class);
        authenticatedUser = new AuthenticatedUser("Myachin",
                "someToken", Role.ROLE_ADMIN.name());

        comment= Comment.builder()
                .id(1L)
                .user(user)
                .text("someText")
                .build();
        commentDtoRequest=modelMapper.map(comment,CommentDtoRequest.class);
        commentDtoRequest.setUserId(1L);
        commentDtoResponse=modelMapper.map(comment,CommentDtoResponse.class);
        commentDtoResponse.setUserDtoResponse(userDtoResponse);
        post = Post.builder()
                .id(1L)
                .content("someContent")
                .title("Yayaya")
                .user(user)
                .comments(List.of(comment))
                .build();

        postDtoRequest = modelMapper.map(post,PostDtoRequest.class);
        postDtoRequest.setUserId(1L);
        postDtoResponse=modelMapper.map(post,PostDtoResponse.class);
        postDtoResponse.setUserDtoResponse(userDtoResponse);
        postDtoResponse.setComments(List.of(commentDtoResponse));
        modelCreatedEvent= ModelCreatedEvent.builder()
                .modelName(Comment.class.getName())
                .modelId(1L)
                .build();
        modelUpdatedEvent = ModelUpdatedEvent.builder()
                .modelName(Comment.class.getName())
                .modelId(1L)
                .build();
        modelDeletedEvent = ModelDeletedEvent.builder()
                .model(comment)
                .modelType(ModelType.COMMENT)
                .build();
    }

    @AfterEach
    void tearDown() {
        User user=null;
        UserDtoResponse userDtoResponse=null;
        AuthenticatedUser authenticatedUser=null;
        Comment comment=null;
        CommentDtoResponse commentDtoResponse=null;
        CommentDtoRequest commentDtoRequest=null;
        Post post=null;
        PostDtoRequest postDtoRequest=null;
        PostDtoResponse postDtoResponse=null;
    }

    @Test
    void findAllCommentsByPost() {
        List<Long> idsList = postDtoResponse.getComments().stream()
                .map(comment -> commentDtoResponse.getId())
                .collect(Collectors.toList());
        Pageable pageable = PageRequest.of(0,3, Sort.by("id"));
        List<Comment> commentsList = List.of(comment);
        Page<Comment> commentPage = new PageImpl<>(commentsList,pageable,1);
        Page<CommentDtoResponse> expected = new PageImpl<>(List.of(commentDtoResponse),pageable,1);
        when(postService.getById(post.getId())).thenReturn(postDtoResponse);
        when(commentRepository.findAllByIdIn(idsList,pageable)).thenReturn(commentPage);
        when(commentMapper.mapToCommentDtoResponse(comment)).thenReturn(commentDtoResponse);


        Page<CommentDtoResponse> actual = commentService.findAllCommentsByPost(post.getId(), pageable);

        assertEquals(expected,actual);
        verify(postService).getById(post.getId());
        verify(commentRepository).findAllByIdIn(idsList,pageable);
        verify(commentMapper).mapToCommentDtoResponse(comment);
    }

    @Test
    void findCommentByPostIdAndCommentId() {
        CommentDtoResponse expected = commentDtoResponse;
        when(postService.getById(post.getId())).thenReturn(postDtoResponse);

        CommentDtoResponse actual = commentService.findCommentByPostIdAndCommentId(post.getId(), comment.getId());

        assertEquals(expected,actual);
        verify(postService).getById(post.getId());
    }

    @Test
    void findCommentByPostIdAndCommentIdFail() {
        String expectedMessage = "Comment wasn't found by id=1 from Post with id=1";
        CommentDtoResponse expected = commentDtoResponse;
        postDtoResponse.setComments(new ArrayList<>());
        when(postService.getById(post.getId())).thenReturn(postDtoResponse);

        ResourceNotFoundException message = assertThrows(ResourceNotFoundException.class,
                () -> commentService.findCommentByPostIdAndCommentId(post.getId(), comment.getId()));


        assertTrue(message.getMessage().contains(expectedMessage));
        verify(postService).getById(post.getId());
    }

    @Test
    void save() {
        CommentDtoResponse expected = commentDtoResponse;
        when(postService.existsById(post.getId())).thenReturn(true);
        when(userService.getById(user.getId())).thenReturn(userDtoResponse);
        when(sequenceGeneratorService.generateSequence(Comment.SEQUENCE_NAME)).thenReturn(1L);
        when(commentMapper.mapToComment(1L,userDtoResponse,commentDtoRequest)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.mapToCommentDtoResponse(comment)).thenReturn(commentDtoResponse);

        CommentDtoResponse actual = commentService.save(commentDtoRequest, post.getId(), authenticatedUser);

        assertEquals(expected,actual);
        verify(postService).existsById(post.getId());
        verify(userService).getById(user.getId());
        verify(sequenceGeneratorService).generateSequence(Comment.SEQUENCE_NAME);
        verify(commentMapper).mapToComment(1L,userDtoResponse,commentDtoRequest);
        verify(commentRepository).save(comment);
        verify(commentMapper).mapToCommentDtoResponse(comment);
        verify(applicationEventPublisher).publishEvent(modelCreatedEvent);
        verify(postService).addCommentToPost(post.getId(),comment);

    }

    @Test
    void saveFailPostId() {
        String expectedMessage = "Post wasn't found by id=1";
        when(postService.existsById(post.getId())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.save(commentDtoRequest, post.getId(), authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(postService).existsById(post.getId());
        verify(userService,never()).getById(user.getId());
        verify(sequenceGeneratorService,never()).generateSequence(Comment.SEQUENCE_NAME);
        verify(commentMapper,never()).mapToComment(1L,userDtoResponse,commentDtoRequest);
        verify(commentRepository,never()).save(comment);
        verify(commentMapper,never()).mapToCommentDtoResponse(comment);
        verify(applicationEventPublisher,never()).publishEvent(modelCreatedEvent);
        verify(postService,never()).addCommentToPost(post.getId(),comment);

    }

    @Test
    void saveFailCredentials() {
        String expectedMessage = "User must be authenticated";
        authenticatedUser=null;

        NotValidCredentialsException exception = assertThrows(NotValidCredentialsException.class,
                () -> commentService.save(commentDtoRequest, post.getId(), authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(postService,never()).existsById(post.getId());
        verify(userService,never()).getById(user.getId());
        verify(sequenceGeneratorService,never()).generateSequence(Comment.SEQUENCE_NAME);
        verify(commentMapper,never()).mapToComment(1L,userDtoResponse,commentDtoRequest);
        verify(commentRepository,never()).save(comment);
        verify(commentMapper,never()).mapToCommentDtoResponse(comment);
        verify(applicationEventPublisher,never()).publishEvent(modelCreatedEvent);
        verify(postService,never()).addCommentToPost(post.getId(),comment);

    }

    @Test
    void update() {
        CommentDtoResponse expected= commentDtoResponse;
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.ofNullable(comment));
        when(commentMapper.mapToComment(comment.getId(),comment.getUser(),commentDtoRequest)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(postService.getById(post.getId())).thenReturn(postDtoResponse);
        when(commentMapper.mapToCommentDtoResponse(comment)).thenReturn(commentDtoResponse);

        CommentDtoResponse actual = commentService.update(comment.getId(), post.getId(), commentDtoRequest, authenticatedUser);

        assertEquals(expected,actual);
        verify(commentRepository).findById(comment.getId());
        verify(commentMapper).mapToComment(comment.getId(),comment.getUser(),commentDtoRequest);
        verify(commentRepository).save(comment);
        verify(commentMapper).mapToCommentDtoResponse(comment);
        verify(applicationEventPublisher).publishEvent(modelUpdatedEvent);
        verify(postService).getById(post.getId());
    }
    @Test
    void updateFailId() {
        String expectedMessage = "Comment wasn't found by id=1";
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.empty());


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.update(comment.getId(), post.getId(), commentDtoRequest, authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(commentRepository).findById(comment.getId());
        verify(postService,never()).existsByPostIdAndComment(post.getId(),comment);
        verify(commentMapper,never()).mapToComment(comment.getId(),comment.getUser(),commentDtoRequest);
        verify(commentRepository,never()).save(comment);
        verify(commentMapper,never()).mapToCommentDtoResponse(comment);
        verify(applicationEventPublisher,never()).publishEvent(modelUpdatedEvent);
        verify(postService,never()).getById(post.getId());
    }
    @Test
    void updateFailCredentials() {
        String expectedMessage = "User has no enough permissions";
        authenticatedUser = new AuthenticatedUser("sadasdas","DFsfsfs","ROLE_USER");
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.ofNullable(comment));
        when(postService.getById(post.getId())).thenReturn(postDtoResponse);

        NotValidCredentialsException exception = assertThrows(NotValidCredentialsException.class,
                () -> commentService.update(comment.getId(), post.getId(), commentDtoRequest, authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(commentRepository).findById(comment.getId());
        verify(commentMapper,never()).mapToComment(comment.getId(),comment.getUser(),commentDtoRequest);
        verify(commentRepository,never()).save(comment);
        verify(commentMapper,never()).mapToCommentDtoResponse(comment);
        verify(applicationEventPublisher,never()).publishEvent(modelUpdatedEvent);
        verify(postService).getById(post.getId());
    }

    @Test
    void updateFailByPostIdAndCommentId() {
        String expectedMessage = "Comment wasn't found by id=1 from Post with id=1";
        postDtoResponse.setComments(List.of());
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.ofNullable(comment));
        when(postService.getById(post.getId())).thenReturn(postDtoResponse);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.update(comment.getId(), post.getId(), commentDtoRequest, authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(commentRepository).findById(comment.getId());
        verify(commentMapper,never()).mapToComment(comment.getId(),comment.getUser(),commentDtoRequest);
        verify(commentRepository,never()).save(comment);
        verify(commentMapper,never()).mapToCommentDtoResponse(comment);
        verify(applicationEventPublisher,never()).publishEvent(modelUpdatedEvent);
        verify(postService).getById(post.getId());
    }

    @Test
    void delete() {
        when(commentRepository.findById(1L)).thenReturn(Optional.ofNullable(comment));
        when(postService.getById(1L)).thenReturn(postDtoResponse);

        commentService.delete(comment.getId(),post.getId(),authenticatedUser);

        verify(commentRepository).findById(1L);
        verify(postService).getById(1L);
        verify(applicationEventPublisher).publishEvent(modelDeletedEvent);
        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteFailCommentId() {
        String expectedMessage = "Comment wasn't found by id=1";
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.delete(comment.getId(), post.getId(), authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(commentRepository).findById(1L);
        verify(postService,never()).getById(1L);
        verify(applicationEventPublisher,never()).publishEvent(modelDeletedEvent);
        verify(commentRepository,never()).delete(comment);
    }

    @Test
    void deleteFailCredentials() {
        String expectedMessage = "User must be authenticated";
        authenticatedUser=null;
        when(commentRepository.findById(1L)).thenReturn(Optional.ofNullable(comment));


        NotValidCredentialsException exception = assertThrows(NotValidCredentialsException.class,
                () -> commentService.delete(1L, 1L, authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(commentRepository).findById(1L);
        verify(postService,never()).getById(1L);
        verify(applicationEventPublisher,never()).publishEvent(modelDeletedEvent);
        verify(commentRepository,never()).delete(comment);
    }

    @Test
    void deleteAllByUser() {
        when(commentRepository.findAllByUserId(user.getId())).thenReturn(List.of(comment));

        commentService.deleteAllByUser(user);

        verify(commentRepository).findAllByUserId(user.getId());
        verify(applicationEventPublisher).publishEvent(modelDeletedEvent);
        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteAllByPost() {
        post.setComments(List.of(comment));

        commentService.deleteAllByPost(post);

        verify(commentRepository).deleteAll(List.of(comment));
    }
}