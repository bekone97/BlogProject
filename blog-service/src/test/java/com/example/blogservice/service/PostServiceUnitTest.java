package com.example.blogservice.service;

import com.example.blogservice.dto.*;
import com.example.blogservice.event.ModelCreatedEvent;
import com.example.blogservice.event.ModelDeletedEvent;
import com.example.blogservice.event.ModelType;
import com.example.blogservice.event.ModelUpdatedEvent;
import com.example.blogservice.exception.NotValidCredentialsException;
import com.example.blogservice.exception.ResourceNotFoundException;
import com.example.blogservice.mapper.PostMapper;
import com.example.blogservice.model.Comment;
import com.example.blogservice.model.Post;
import com.example.blogservice.model.Role;
import com.example.blogservice.model.User;
import com.example.blogservice.repository.PostRepository;
import com.example.blogservice.security.user.AuthenticatedUser;
import com.example.blogservice.service.impl.PostServiceImpl;
import com.example.blogservice.service.impl.SequenceGeneratorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PostServiceUnitTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private UserService userService;

    @Mock
    private SequenceGeneratorService sequenceGeneratorService;

    @Mock
    private FileService fileService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private PostServiceImpl postService;

    private ModelMapper modelMapper = new ModelMapper();

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
    public void setUp() {
        user = User.builder()
                .username("Myachin")
                .password("Artsiom")
                .email("myachinenergo@mail.ru")
                .id(1L)
                .dateOfBirth(LocalDate.now().minusYears(12))
                .role(Role.ROLE_ADMIN)
                .build();
        userDtoResponse = modelMapper.map(user, UserDtoResponse.class);
        authenticatedUser = new AuthenticatedUser("Myachin",
                "someToken", Role.ROLE_ADMIN.name());

        comment = Comment.builder()
                .id(1L)
                .user(user)
                .text("someText")
                .build();
        commentDtoRequest = modelMapper.map(comment, CommentDtoRequest.class);
        commentDtoRequest.setUserId(1L);
        commentDtoResponse = modelMapper.map(comment, CommentDtoResponse.class);
        post = Post.builder()
                .id(1L)
                .content("someContent")
                .title("Yayaya")
                .user(user)
                .comments(new ArrayList<>())
                .build();
        postDtoRequest = modelMapper.map(post, PostDtoRequest.class);
        postDtoRequest.setUserId(1L);
        postDtoResponse = modelMapper.map(post, PostDtoResponse.class);
        postDtoResponse.setUserDtoResponse(userDtoResponse);
        modelCreatedEvent = ModelCreatedEvent.builder()
                .modelName(Post.class.getName())
                .modelId(1L)
                .build();
        modelUpdatedEvent = ModelUpdatedEvent.builder()
                .modelName(Post.class.getName())
                .modelId(1L)
                .build();
        modelDeletedEvent = ModelDeletedEvent.builder()
                .model(post)
                .modelType(ModelType.POST)
                .build();
    }

    @AfterEach
    public void tearDown() {
        user = null;
        comment = null;
        commentDtoRequest = null;
        commentDtoResponse = null;
        post = null;
        postDtoRequest = null;
        postDtoResponse = null;
        modelCreatedEvent = null;
        modelDeletedEvent = null;
        modelUpdatedEvent = null;
    }

    @Test
    void getById() {
        PostDtoResponse expected = postDtoResponse;
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postMapper.mapToPostDtoResponse(post)).thenReturn(postDtoResponse);

        PostDtoResponse actual = postService.getById(1L);

        assertEquals(expected, actual);
        verify(postRepository).findById(1L);
        verify(postMapper).mapToPostDtoResponse(post);
    }

    @Test
    void getByIdFail() {
        String expectedMessage = "Post wasn't found by id=1";
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> postService.getById(1L));

        assertTrue(actual.getMessage().contains(expectedMessage));
        verify(postRepository).findById(1L);
        verify(postMapper, never()).mapToPostDtoResponse(post);
    }

    @Test
    void findAll() {
        postDtoResponse.setUserDtoResponse(userDtoResponse);
        List<Post> posts = List.of(post);
        Pageable pageable = PageRequest.of(1, 3, Sort.by("id"));
        Page<Post> page = new PageImpl<>(posts, pageable, 1);
        Page<PostDtoResponse> expected = new PageImpl<>(List.of(postDtoResponse), pageable, 1);

        when(postRepository.findAll(pageable)).thenReturn(page);
        when(postMapper.mapToPostDtoResponse(post)).thenReturn(postDtoResponse);

        Page<PostDtoResponse> actual = postService.findAll(pageable);

        assertEquals(expected.getContent(), actual.getContent());
        verify(postRepository).findAll(pageable);
        verify(postMapper).mapToPostDtoResponse(post);
    }

    @Test
    void save() {
        PostDtoResponse expected = postDtoResponse;
        when(userService.getById(1L)).thenReturn(userDtoResponse);
        when(postMapper.mapToPost(1L, postDtoRequest, userDtoResponse)).thenReturn(post);
        when(sequenceGeneratorService.generateSequence(Post.SEQUENCE_NAME)).thenReturn(1L);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.mapToPostDtoResponse(post)).thenReturn(postDtoResponse);


        PostDtoResponse actual = postService.save(postDtoRequest, authenticatedUser);

        assertEquals(expected, actual);
        verify(userService).getById(1L);
        verify(postMapper).mapToPost(1L, postDtoRequest, userDtoResponse);
        verify(sequenceGeneratorService).generateSequence(Post.SEQUENCE_NAME);
        verify(postRepository).save(post);
        verify(postMapper).mapToPostDtoResponse(post);
    }

    @Test
    void saveFail() {
        String expectedMessage = "User must be authenticated to save post";
        authenticatedUser = null;

        NotValidCredentialsException actual = assertThrows(NotValidCredentialsException.class,
                () -> postService.save(postDtoRequest, authenticatedUser));

        assertTrue(actual.getMessage().contains(expectedMessage));
        verify(userService, never()).getById(1L);
        verify(postMapper, never()).mapToPost(1L, postDtoRequest, userDtoResponse);
        verify(sequenceGeneratorService, never()).generateSequence(Post.SEQUENCE_NAME);
        verify(postRepository, never()).save(post);
        verify(postMapper, never()).mapToPostDtoResponse(post);
    }

    @Test
    void update() {
        PostDtoResponse expect = postDtoResponse;
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postMapper.mapToPost(1L, postDtoRequest, userDtoResponse, post.getComments())).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.mapToPostDtoResponse(post)).thenReturn(postDtoResponse);
        when(userService.getById(1L)).thenReturn(userDtoResponse);

        PostDtoResponse actual = postService.update(1L, postDtoRequest, authenticatedUser);

        assertEquals(expect, actual);
        verify(postRepository).findById(1L);
        verify(postMapper).mapToPost(1L, postDtoRequest, userDtoResponse, post.getComments());
        verify(postMapper).mapToPostDtoResponse(post);
        verify(postRepository).save(post);
        verify(userService).getById(1L);
        verify(applicationEventPublisher).publishEvent(modelUpdatedEvent);
    }

    @Test
    void updateFailId() {
        String expectedMessage = "Post wasn't found by id=1";
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        when(userService.getById(1L)).thenReturn(userDtoResponse);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> postService.update(1L, postDtoRequest, authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));

        verify(postRepository).findById(1L);
        verify(postMapper, never()).mapToPost(1L, postDtoRequest, userDtoResponse, post.getComments());
        verify(postMapper, never()).mapToPostDtoResponse(post);
        verify(postRepository, never()).save(post);
        verify(userService).getById(1L);
        verify(applicationEventPublisher, never()).publishEvent(modelUpdatedEvent);
    }

    @Test
    void updateFailCredentials() {
        String expectedMessage = "User has no enough permissions";
        authenticatedUser = null;
        when(userService.getById(1L)).thenReturn(userDtoResponse);

        NotValidCredentialsException exception = assertThrows(NotValidCredentialsException.class,
                () -> postService.update(1L, postDtoRequest, authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));

        verify(postRepository, never()).findById(1L);
        verify(postMapper, never()).mapToPost(1L, postDtoRequest, userDtoResponse, post.getComments());
        verify(postMapper, never()).mapToPostDtoResponse(post);
        verify(postRepository, never()).save(post);
        verify(userService).getById(1L);
        verify(applicationEventPublisher, never()).publishEvent(modelUpdatedEvent);
    }

    @Test
    void deleteById() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postMapper.mapToPostDtoResponse(post)).thenReturn(postDtoResponse);

        postService.deleteById(1L, authenticatedUser);

        verify(postRepository).findById(1L);
        verify(applicationEventPublisher).publishEvent(modelDeletedEvent);
        verify(postRepository).delete(post);
    }

    @Test
    void deleteByIdFail() {
        String expectedMessage = "Post wasn't found by id=1";
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> postService.deleteById(1L, authenticatedUser));

        assertTrue(actual.getMessage().contains(expectedMessage));
        verify(postRepository).findById(1L);
        verify(applicationEventPublisher, never()).publishEvent(modelDeletedEvent);
        verify(postRepository, never()).delete(post);
    }

    @Test
    void deleteByIdFailCredentials() {
        authenticatedUser = null;
        String expectedMessage = "User has no enough permissions";
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(post));

        NotValidCredentialsException actual = assertThrows(NotValidCredentialsException.class,
                () -> postService.deleteById(1L, authenticatedUser));

        assertTrue(actual.getMessage().contains(expectedMessage));
        verify(postRepository).findById(1L);
        verify(applicationEventPublisher, never()).publishEvent(modelDeletedEvent);
        verify(postRepository, never()).delete(post);
    }

    @Test
    void findAllByUserId() {
        List<PostDtoResponse> expected = List.of(postDtoResponse);
        when(userService.existsById(1L)).thenReturn(true);
        when(postRepository.findAllByUserId(1L)).thenReturn(List.of(post));
        when(postMapper.mapToPostDtoResponse(post)).thenReturn(postDtoResponse);

        List<PostDtoResponse> actual = postService.findAllByUserId(1L);

        assertEquals(expected, actual);
        verify(userService).existsById(1L);
        verify(postRepository).findAllByUserId(1L);
        verify(postMapper).mapToPostDtoResponse(post);
    }

    @Test
    void findAllByUserIdFail() {
        String expectedMessage = "User wasn't found by userId=1";
        when(userService.existsById(1L)).thenReturn(false);


        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> postService.findAllByUserId(1L));

        assertTrue(actual.getMessage().contains(expectedMessage));
        verify(userService).existsById(1L);
        verify(postRepository, never()).findAllByUserId(1L);
        verify(postMapper, never()).mapToPostDtoResponse(post);
    }


    @Test
    void addCommentToPost() {
        Post changedPost = Post.builder()
                .id(1L)
                .content("someContent")
                .title("Yayaya")
                .comments(List.of(comment))
                .user(user)
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(changedPost)).thenReturn(changedPost);

        postService.addCommentToPost(1L, comment);

        verify(postRepository).findById(1L);
        verify(postRepository).save(changedPost);
        verify(applicationEventPublisher).publishEvent(modelUpdatedEvent);
    }

    @Test
    void deleteAllByUser() {
        when(postRepository.findAllByUserId(1L)).thenReturn(List.of(post));

        postService.deleteAllByUser(user);

        verify(postRepository).findAllByUserId(user.getId());
        verify(postRepository).delete(post);
        verify(applicationEventPublisher).publishEvent(modelDeletedEvent);
    }

    @Test
    void deleteCommentFromPostByComment() {
        Post previousPost = Post.builder()
                .id(1L)
                .content("someContent")
                .title("Yayaya")
                .comments(List.of(comment))
                .user(user)
                .build();
        when(postRepository.findPostByCommentsIsContaining(comment)).thenReturn(Optional.of(previousPost));
        when(postRepository.save(post)).thenReturn(post);

        postService.deleteCommentFromPostByComment(comment);

        verify(postRepository).findPostByCommentsIsContaining(comment);
        verify(postRepository).save(post);

    }
}