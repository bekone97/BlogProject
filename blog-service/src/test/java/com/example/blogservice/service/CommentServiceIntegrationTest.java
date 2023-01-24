package com.example.blogservice.service;

import com.example.blogservice.dto.*;
import com.example.blogservice.event.ModelCreatedEvent;
import com.example.blogservice.event.ModelDeletedEvent;
import com.example.blogservice.event.ModelType;
import com.example.blogservice.event.ModelUpdatedEvent;
import com.example.blogservice.exception.NotValidCredentialsException;
import com.example.blogservice.exception.ResourceNotFoundException;
import com.example.blogservice.initializer.DatabaseContainerInitializer;
import com.example.blogservice.model.Comment;
import com.example.blogservice.model.Post;
import com.example.blogservice.model.Role;
import com.example.blogservice.model.User;
import com.example.blogservice.repository.CommentRepository;
import com.example.blogservice.repository.PostRepository;
import com.example.blogservice.repository.UserRepository;
import com.example.blogservice.security.user.AuthenticatedUser;
import com.example.blogservice.service.impl.SequenceGeneratorService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommentServiceIntegrationTest extends DatabaseContainerInitializer {

    @TestConfiguration
    static class TestConfig {

        @Bean
        public TaskExecutor taskExecutor() {
            return new SyncTaskExecutor();
        }

    }

    @SpyBean
    private PostRepository postRepository;

    @SpyBean
    private UserRepository userRepository;

    @SpyBean
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private CommentService commentService;
    @SpyBean
    private CommentRepository commentRepository;
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
        userRepository.deleteAll();
        postRepository.deleteAll();
        commentRepository.deleteAll();
        user = User.builder()
                .username("Myachin")
                .password("Artsiom")
                .email("myachinenergo@mail.ru")
                .id(1L)
                .dateOfBirth(LocalDate.now().minusYears(12))
                .role(Role.ROLE_ADMIN)
                .build();
        userDtoResponse = UserDtoResponse.builder()
                .username("Myachin")
                .email("myachinenergo@mail.ru")
                .dateOfBirth(LocalDate.now().minusYears(12))
                .id(1L)
                .build();
        authenticatedUser = new AuthenticatedUser("Myachin",
                "someToken", Role.ROLE_ADMIN.name());

        comment = Comment.builder()
                .id(1L)
                .user(user)
                .text("someText")
                .build();
        commentDtoRequest = CommentDtoRequest.builder()
                .userId(1L)
                .text("someText")
                .build();
        commentDtoResponse = CommentDtoResponse.builder()
                .id(1L)
                .text("someText")
                .userDtoResponse(userDtoResponse)
                .build();
        post = Post.builder()
                .id(1L)
                .content("someContent")
                .title("Yayaya")
                .user(user)
                .comments(List.of(comment))
                .build();
        postDtoResponse = PostDtoResponse.builder()
                .id(1L)
                .comments(List.of(commentDtoResponse))
                .content("some content")
                .title("Yayaya")
                .userDtoResponse(userDtoResponse)
                .build();
        modelCreatedEvent = ModelCreatedEvent.builder()
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
        User user = null;
        UserDtoResponse userDtoResponse = null;
        AuthenticatedUser authenticatedUser = null;
        Comment comment = null;
        CommentDtoResponse commentDtoResponse = null;
        CommentDtoRequest commentDtoRequest = null;
        Post post = null;
        PostDtoRequest postDtoRequest = null;
        PostDtoResponse postDtoResponse = null;
        ModelCreatedEvent modelCreatedEvent = null;
        ModelUpdatedEvent modelUpdatedEvent = null;
        ModelDeletedEvent modelDeletedEvent = null;

    }

    @Test
    @Order(1)
    void findAllCommentsByPost() {
        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment);
        Pageable pageable = PageRequest.of(0, 3, Sort.by("id"));
        Page<CommentDtoResponse> expected = new PageImpl<>(List.of(commentDtoResponse), pageable, 1);

        Page<CommentDtoResponse> actual = commentService.findAllCommentsByPost(post.getId(), pageable);

        assertEquals(expected.getContent(), actual.getContent());
    }

    @Test
    @Order(2)
    void findAllCommentsByPostFail() {
        String expectedMessage = "Post wasn't found by id=10";
        userRepository.save(user);
        Pageable pageable = PageRequest.of(0, 3, Sort.by("id"));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.findAllCommentsByPost(10L, pageable));

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @Order(3)
    void findCommentByPostIdAndCommentId() {
        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment);
        CommentDtoResponse expected = commentDtoResponse;
        CommentDtoResponse actual = commentService.findCommentByPostIdAndCommentId(post.getId(), comment.getId());

        assertEquals(expected, actual);
    }

    @Test
    @Order(4)
    void findCommentByPostIdAndFailCommentId() {
        String expectedMessage = "Comment wasn't found by id=10 from Post with id=1";
        userRepository.save(user);
        post.setComments(List.of());
        postRepository.save(post);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.findCommentByPostIdAndCommentId(post.getId(), 10L));

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @Order(5)
    void save() {
        CommentDtoResponse expected = commentDtoResponse;
        userRepository.save(user);
        post.setComments(List.of());
        postRepository.save(post);
        when(sequenceGeneratorService.generateSequence(Comment.SEQUENCE_NAME)).thenReturn(1L);

        CommentDtoResponse actual = commentService.save(commentDtoRequest, 1L, authenticatedUser);

        assertEquals(expected, actual);
    }

    @Test
    @Order(6)
    void saveFailPostId() {
        String expectedMessage = "Post wasn't found by id=10";
        userRepository.save(user);


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.save(commentDtoRequest, 10L, authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @Order(7)
    void saveFailCredentials() {
        String expectedMessage = "User must be authenticated";
        userRepository.save(user);
        authenticatedUser = null;


        NotValidCredentialsException exception = assertThrows(NotValidCredentialsException.class,
                () -> commentService.save(commentDtoRequest, post.getId(), authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @Order(8)
    void update() {
        CommentDtoResponse expected = commentDtoResponse;
        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment);

        CommentDtoResponse actual = commentService.update(comment.getId(), post.getId(), commentDtoRequest, authenticatedUser);

        assertEquals(expected, actual);
    }

    @Test
    @Order(9)
    void updateFailId() {
        String expectedMessage = "Comment wasn't found by id=10";
        userRepository.save(user);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.update(10L, post.getId(), commentDtoRequest, authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @Order(10)
    void updateFailCredentials() {
        String expectedMessage = "User has no enough permissions";
        authenticatedUser = new AuthenticatedUser("sadasdas", "DFsfsfs", "ROLE_USER");
        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment);

        NotValidCredentialsException exception = assertThrows(NotValidCredentialsException.class,
                () -> commentService.update(comment.getId(), post.getId(), commentDtoRequest, authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @Order(11)
    void updateFailCommentId() {
        String expectedMessage = "Comment wasn't found by id=10";
        userRepository.save(user);
        post.setComments(List.of());
        postRepository.save(post);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.update(10L, post.getId(), commentDtoRequest, authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @Order(12)
    void delete() {
        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment);

        commentService.delete(comment.getId(), post.getId(), authenticatedUser);

        assertFalse(commentRepository.existsById(comment.getId()));
        assertFalse(postRepository.findById(post.getId()).get().getComments().contains(comment));
    }

    @Test
    @Order(13)
    void deleteFailCommentId() {
        String expectedMessage = "Comment wasn't found by id=10";
        userRepository.save(user);
        post.setComments(List.of());
        postRepository.save(post);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> commentService.delete(10L, post.getId(), authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @Order(14)
    void deleteFailCredentials() {
        String expectedMessage = "User must be authenticated";
        authenticatedUser = null;
        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment);

        NotValidCredentialsException exception = assertThrows(NotValidCredentialsException.class,
                () -> commentService.delete(1L, 1L, authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @Order(15)
    void deleteAllByUser() {
        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment);

        commentService.deleteAllByUser(user);

        assertFalse(commentRepository.existsById(comment.getId()));
        assertFalse(postRepository.findById(post.getId()).get().getComments().contains(comment));
    }

    @Test
    @Order(16)
    void deleteAllByPost() {
        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment);

        commentService.deleteAllByPost(post);

        assertFalse(commentRepository.existsById(comment.getId()));
    }


}
