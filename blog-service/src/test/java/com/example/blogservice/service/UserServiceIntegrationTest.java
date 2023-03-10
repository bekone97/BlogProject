package com.example.blogservice.service;

import com.example.blogservice.dto.UserDto;
import com.example.blogservice.dto.UserDtoRequest;
import com.example.blogservice.dto.UserDtoResponse;
import com.example.blogservice.exception.NotUniqueResourceException;
import com.example.blogservice.exception.NotValidTokenException;
import com.example.blogservice.exception.ResourceNotFoundException;
import com.example.blogservice.initializer.DatabaseContainerInitializer;
import com.example.blogservice.model.Role;
import com.example.blogservice.model.User;
import com.example.blogservice.repository.UserRepository;
import com.example.blogservice.security.user.AuthenticatedUser;
import com.example.blogservice.service.impl.SequenceGeneratorService;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceIntegrationTest extends DatabaseContainerInitializer {

    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @SpyBean
    private UserRepository userRepository;

    @SpyBean
    private SequenceGeneratorService sequenceGeneratorService;

    @SpyBean
    private PasswordEncoder passwordEncoder;

    UserDtoRequest userDtoRequest;
    UserDtoResponse userDtoResponse;
    User user;
    UserDto userDto;
    AuthenticatedUser authenticatedUser;
    String incomingPassword;

    @BeforeEach
    public void setUp() {
        incomingPassword = "artsiom";
        user = User.builder()
                .username("Myachin")
                .password(incomingPassword)
                .email("myachinenergo@mail.ru")
                .id(1L)
                .dateOfBirth(LocalDate.now().minusYears(12))
                .role(Role.ROLE_USER)
                .build();
        userDtoRequest = modelMapper.map(user, UserDtoRequest.class);
        userDtoResponse = modelMapper.map(user, UserDtoResponse.class);
        userDto = modelMapper.map(user, UserDto.class);
        authenticatedUser = new AuthenticatedUser("Myachin",
                "someToken", Role.ROLE_ADMIN.name());
        userRepository.deleteAll();
    }


    @Test
    @Order(1)
    void save() {
        String incomingPassword = "artsiom";
        when(passwordEncoder.encode(incomingPassword)).thenReturn("artsiom");
        UserDtoResponse expected = userDtoResponse;

        UserDtoResponse actual = userService.save(userDtoRequest, incomingPassword);

        assertEquals(expected, actual);
        verify(userRepository).save(any(User.class));
        verify(sequenceGeneratorService).generateSequence(User.SEQUENCE_NAME);
    }

    @Test
    @Order(2)
    void saveFail() {
        userRepository.save(user);

        NotUniqueResourceException actual = assertThrows(NotUniqueResourceException.class,
                () -> userService.save(userDtoRequest, "somePassword"));

        assertTrue(actual.getMessage().contains("User already exists with username=Myachin"));
    }

    @Test
    @Order(3)
    void getById() {
        userRepository.save(user);
        UserDtoResponse expected = userDtoResponse;

        UserDtoResponse actual = userService.getById(1L);

        assertEquals(actual, expected);
    }

    @Test
    @Order(4)
    void getInnerUserById() {
        userRepository.save(user);
        UserDto expected = userDto;

        var actual = userService.getInnerUserById(1L);

        assertEquals(expected, actual);
    }

    @Test
    @Order(5)
    void getUserByIdFailed() {
        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> userService.getById(100L));

        assertTrue(actual.getMessage().contains("User wasn't found by id=100"));
    }

    @Test
    @Order(6)
    void update() {
        userRepository.save(user);
        userDtoRequest.setDateOfBirth(LocalDate.now().minusYears(13));
        user.setDateOfBirth(LocalDate.now().minusYears(13));
        UserDtoResponse expected = userDtoResponse;
        expected.setDateOfBirth(LocalDate.now().minusYears(13));

        UserDtoResponse actual = userService.update(1L, userDtoRequest, authenticatedUser);

        assertEquals(expected, actual);

    }

    @Test
    @Order(7)
    void deleteById() {
        userRepository.save(user);
        userService.deleteById(user.getId(), authenticatedUser);

        assertFalse(userRepository.existsById(user.getId()));
    }

    @Test
    @Order(8)
    void deleteByIdFail() {

        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteById(100L, authenticatedUser));

        assertTrue(actual.getMessage().contains("User wasn't found by id=100"));
    }

    @Test
    @Order(9)
    void loadUserByUsername() {
        userRepository.save(user);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority((user.getRole().name())));
        org.springframework.security.core.userdetails.User expected =
                new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        UserDetails actual = userService.loadUserByUsername(user.getUsername());

        assertEquals(actual, expected);
    }

    @Test
    @Order(10)
    void loadUserByUsernameFail() {
        String expectedMessage = "Token isn't valid";
        String username = "someNotExisting";
        userRepository.save(user);

        NotValidTokenException exception = assertThrows(NotValidTokenException.class,
                () -> userService.loadUserByUsername(username));

        assertTrue(exception.getMessage().contains("Token isn't valid"));

    }
}
