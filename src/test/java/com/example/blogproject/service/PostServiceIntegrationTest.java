package com.example.blogproject.service;

import com.example.blogproject.dto.*;
import com.example.blogproject.event.ModelCreatedEvent;
import com.example.blogproject.event.ModelDeletedEvent;
import com.example.blogproject.event.ModelType;
import com.example.blogproject.event.ModelUpdatedEvent;
import com.example.blogproject.exception.NotValidCredentialsException;
import com.example.blogproject.exception.ResourceNotFoundException;
import com.example.blogproject.initializer.DatabaseContainerInitializer;
import com.example.blogproject.mapper.PostMapper;
import com.example.blogproject.model.Comment;
import com.example.blogproject.model.Post;
import com.example.blogproject.model.Role;
import com.example.blogproject.model.User;
import com.example.blogproject.repository.CommentRepository;
import com.example.blogproject.repository.PostRepository;
import com.example.blogproject.repository.UserRepository;
import com.example.blogproject.security.user.AuthenticatedUser;
import com.example.blogproject.service.impl.SequenceGeneratorService;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostServiceIntegrationTest extends DatabaseContainerInitializer {

    @TestConfiguration
    static class TestConfig {

        @Bean
        public TaskExecutor taskExecutor(){
            return new SyncTaskExecutor();
        }
    }


    @SpyBean
    private PostRepository postRepository;

    @SpyBean
    private PostMapper postMapper;

    @SpyBean
    private UserRepository userRepository;

    @SpyBean
    private SequenceGeneratorService sequenceGeneratorService;

    @SpyBean
    private FileService fileService;


    @Autowired
    private PostService postService;
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ModelMapper modelMapper ;

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
    AtomicLong atomicLong = new AtomicLong(1L);
    @BeforeEach
    public void setUp(){
        user=User.builder()
                .username("Myachin")
                .password("Artsiom")
                .email("myachinenergo@mail.ru")
                .id(1L)
                .dateOfBirth(LocalDate.now().minusYears(12))
                .role(Role.ROLE_ADMIN)
                .build();
        userRepository.save(user);
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
        post = Post.builder()
                .id(1L)
                .title("Yayaya")
                .content("someContent")
                .user(user)
                .comments(new ArrayList<>())
                .build();
        postDtoRequest = modelMapper.map(post,PostDtoRequest.class);
        postDtoRequest.setUserId(1L);
        postDtoResponse=modelMapper.map(post,PostDtoResponse.class);
        postDtoResponse.setUserDtoResponse(userDtoResponse);
    }

    @AfterEach
    public void tearDown(){
        user = null;
        comment=null;
        commentDtoRequest=null;
        commentDtoResponse=null;
        post=null;
        postDtoRequest = null;
        postDtoResponse = null;
        modelCreatedEvent=null;
        modelDeletedEvent=null;
        modelUpdatedEvent=null;

    }

    @Test
    @Order(1)
    void getById() {
        postRepository.save(post);
        PostDtoResponse expect = postDtoResponse;

        PostDtoResponse actual = postService.getById(post.getId());

        assertEquals(expect,actual);

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
        Page<PostDtoResponse> expected = new PageImpl<>(List.of(postDtoResponse),pageable,1);

        Page<PostDtoResponse> actual = postService.findAll(pageable);

        assertEquals(expected.getContent(),actual.getContent());
    }

    @Test
    @Order(4)
    void save() {

        PostDtoResponse expected = postDtoResponse;
        when(sequenceGeneratorService.generateSequence(Post.SEQUENCE_NAME)).thenReturn(postDtoResponse.getId());

        PostDtoResponse actual = postService.save(postDtoRequest, authenticatedUser);

        assertEquals(expected,actual);

    }

    @Test
    @Order(5)
    void saveFail() {

        String expectedMessage = "User must be authenticated to save post";
        authenticatedUser=null;

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

        assertEquals(expected,actual);

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

        String expectedMessage = "Credentials of principle are not valid";
        authenticatedUser=null;

        NotValidCredentialsException exception = assertThrows(NotValidCredentialsException.class,
                () -> postService.update(post.getId(), postDtoRequest, authenticatedUser));

        assertTrue(exception.getMessage().contains(expectedMessage));

    }

    @Test
    @Order(9)
    void deleteById() {

        postRepository.save(post);

        postService.deleteById(post.getId(),authenticatedUser);


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
        authenticatedUser=null;
        String expectedMessage = "Credentials of principle are not valid";
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

        assertEquals(expected,actual);
        verify(postRepository).findAllByUserId(user.getId());

    }

    @Test
    @Order(13)
    void findAllByUserIdFail() {
        String expectedMessage = "User wasn't found by userId=100";


        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> postService.findAllByUserId(100L));

        assertTrue(actual.getMessage().contains(expectedMessage));
        verify(postRepository,never()).findAllByUserId(100L);
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

        postService.addCommentToPost(post.getId(),comment);

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
