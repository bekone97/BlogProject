package com.example.blogservice.service;

import com.example.blogservice.dto.*;
import com.example.blogservice.event.ModelCreatedEvent;
import com.example.blogservice.event.ModelDeletedEvent;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostServiceIntegrationTest extends DatabaseContainerInitializer {

    @SpyBean
    private PostRepository postRepository;


    @SpyBean
    private UserRepository userRepository;

    @SpyBean
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ModelMapper modelMapper;

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
        userRepository.save(user);
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
                .title("Yayaya")
                .content("someContent")
                .user(user)
                .comments(new ArrayList<>())
                .build();
        postDtoRequest = modelMapper.map(post, PostDtoRequest.class);
        postDtoRequest.setUserId(1L);
        postDtoResponse = modelMapper.map(post, PostDtoResponse.class);
        postDtoResponse.setUserDtoResponse(userDtoResponse);
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
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    void getById() {
        postRepository.save(post);
        PostDtoResponse expect = postDtoResponse;

        PostDtoResponse actual = postService.getById(post.getId());

        assertEquals(expect, actual);

    }

    @Test
    @Order(2)
    void getByIdFail() {

        String expectedMessage = "Post wasn't found by id=100";

        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> postService.getById(100L));

        assertTrue(actual.getMessage().contains(expectedMessage));

    }

    @Test
    @Order(3)
    void findAll() {

        postRepository.save(post);
        List<Post> posts = List.of(post);
        Pageable pageable = PageRequest.of(0, 3, Sort.by("id"));
        Page<PostDtoResponse> expected = new PageImpl<>(List.of(postDtoResponse), pageable, 1);

        Page<PostDtoResponse> actual = postService.findAll(pageable);

        assertEquals(expected.getContent(), actual.getContent());
    }

    @Test
    @Order(4)
    void save() {

        PostDtoResponse expected = postDtoResponse;
        when(sequenceGeneratorService.generateSequence(Post.SEQUENCE_NAME)).thenReturn(postDtoResponse.getId());

        PostDtoResponse actual = postService.save(postDtoRequest, authenticatedUser);

        assertEquals(expected, actual);

    }

    @Test
    @Order(5)
    void saveFail() {

        String expectedMessage = "User must be authenticated to save post";
        authenticatedUser = null;

        NotValidCredentialsException actual = assertThrows(NotValidCredentialsException.class,
                () -> postService.save(postDtoRequest, authenticatedUser));

        assertTrue(actual.getMessage().contains(expectedMessage));

    }

    @Test
    @Order(6)
    void update() {

        String newContent = "another content";
        postRepository.save(post);
        postDtoRequest.setContent(newContent);
        PostDtoResponse expected = postDtoResponse;
        expected.setContent(newContent);

        PostDtoResponse actual = postService.update(post.getId(), postDtoRequest, authenticatedUser);

        assertEquals(expected, actual);

    }

    @Test
    @Order(7)
    void updateFailId() {

        String expectedMessage = "Post wasn't found by id=100";

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> postService.update(100L, postDtoRequest, authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));

    }

    @Test
    @Order(8)
    void updateFailCredentials() {

        String expectedMessage = "User has no enough permissions";
        authenticatedUser = null;

        NotValidCredentialsException exception = assertThrows(NotValidCredentialsException.class,
                () -> postService.update(post.getId(), postDtoRequest, authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));

    }

    @Test
    @Order(9)
    void deleteById() {

        postRepository.save(post);

        postService.deleteById(post.getId(), authenticatedUser);


    }

    @Test
    @Order(10)
    void deleteByIdFail() {

        String expectedMessage = "Post wasn't found by id=100";

        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> postService.deleteById(100L, authenticatedUser));

        assertTrue(actual.getMessage().contains(expectedMessage));

    }

    @Test
    @Order(11)
    void deleteByIdFailCredentials() {

        postRepository.save(post);
        authenticatedUser = null;
        String expectedMessage = "User has no enough permissions";
        when(postRepository.findById(post.getId())).thenReturn(Optional.ofNullable(post));

        NotValidCredentialsException actual = assertThrows(NotValidCredentialsException.class,
                () -> postService.deleteById(post.getId(), authenticatedUser));

        assertTrue(actual.getMessage().contains(expectedMessage));

    }

    @Test
    @Order(12)
    void findAllByUserId() {

        postRepository.save(post);
        List<PostDtoResponse> expected = List.of(postDtoResponse);

        List<PostDtoResponse> actual = postService.findAllByUserId(user.getId());

        assertEquals(expected, actual);
        verify(postRepository).findAllByUserId(user.getId());

    }

    @Test
    @Order(13)
    void findAllByUserIdFail() {
        String expectedMessage = "User wasn't found by userId=100";


        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> postService.findAllByUserId(100L));

        assertTrue(actual.getMessage().contains(expectedMessage));
        verify(postRepository, never()).findAllByUserId(100L);
    }

    @Test
    @Order(14)
    void existsById() {

        postRepository.save(post);

        boolean actual = postService.existsById(post.getId());

        assertTrue(actual);

    }

    @Test
    @Order(15)
    void existsByIdFalse() {
        boolean actual = postService.existsById(100L);

        assertFalse(actual);

    }

    @Test
    @Order(16)
    void addCommentToPost() {
        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment);

        postService.addCommentToPost(post.getId(), comment);

        assertTrue(postRepository.findById(post.getId()).get().getComments().contains(comment));
    }

    @Test
    @Order(17)
    void existsByPostIdAndComment() {
        userRepository.save(user);
        commentRepository.save(comment);
        post.setComments(List.of(comment));
        postRepository.save(post);

        boolean actual = postService.existsByPostIdAndComment(post.getId(), comment);

        assertTrue(actual);
    }

    @Test
    @Order(18)
    void deleteAllByUser() {
        userRepository.save(user);
        commentRepository.save(comment);
        post.setComments(List.of(comment));
        postRepository.save(post);

        postService.deleteAllByUser(user);
        assertFalse(commentRepository.existsById(comment.getId()));
        assertFalse(postRepository.existsById(post.getId()));
    }

    @Test
    @Order(19)
    void deleteCommentFromPostByComment() {
        userRepository.save(user);
        commentRepository.save(comment);
        post.setComments(List.of(comment));
        postRepository.save(post);

        postService.deleteCommentFromPostByComment(comment);

        assertFalse(postRepository.existsByIdAndCommentsContaining(post.getId(), comment));
    }
}
