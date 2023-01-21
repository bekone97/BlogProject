package com.example.blogproject.service;

import com.example.blogproject.dto.UserDto;
import com.example.blogproject.dto.UserDtoRequest;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.exception.NotUniqueResourceException;
import com.example.blogproject.exception.NotValidCredentialsException;
import com.example.blogproject.exception.ResourceNotFoundException;
import com.example.blogproject.mapper.UserMapper;
import com.example.blogproject.model.Role;
import com.example.blogproject.model.User;
import com.example.blogproject.repository.UserRepository;
import com.example.blogproject.security.user.AuthenticatedUser;
import com.example.blogproject.service.impl.SequenceGeneratorService;
import com.example.blogproject.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SequenceGeneratorService sequenceGeneratorService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;



    private ModelMapper modelMapper =new ModelMapper();

    User user;
    UserDto userDto;
    UserDtoResponse userDtoResponse;
    UserDtoRequest userDtoRequest;
    AuthenticatedUser authenticatedUser ;


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
        userDtoRequest = modelMapper.map(user,UserDtoRequest.class);
        userDtoResponse = modelMapper.map(user,UserDtoResponse.class);
        userDto = modelMapper.map(user,UserDto.class);
        authenticatedUser = new AuthenticatedUser("Myachin",
                "someToken", Role.ROLE_ADMIN.name());
    }

    @AfterEach
    public void tearDown(){
        user = null;
        userDtoRequest=null;
        userDtoResponse =null;
    }

    @Test
    void getById() {
        UserDtoResponse expected= userDtoResponse;
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(userMapper.mapToUserDtoResponse(user)).thenReturn(userDtoResponse);


        UserDtoResponse actual = userService.getById(1L);

        assertEquals(actual, expected);
        verify(userRepository).findById(1L);
        verify(userMapper).mapToUserDtoResponse(user);
    }

    @Test
    void getByIdFail() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> userService.getById(1L));

        assertTrue(actual.getMessage().contains("User wasn't found by id=1"));
        verify(userRepository).findById(1L);
        verify(userMapper,never()).mapToUserDtoResponse(user);
    }

    @Test
    void getInnerUserById() {
        UserDto expected = userDto;
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.mapToUserDto(user)).thenReturn(userDto);

        var actual = userService.getInnerUserById(1L);

        assertEquals(expected,actual);
        verify(userRepository).findById(1L);
        verify(userMapper).mapToUserDto(user);
    }

    @Test
    void getInnerUserByIdFail() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException actual =assertThrows(ResourceNotFoundException.class,
                ()->userService.getInnerUserById(1L));

        assertTrue(actual.getMessage().contains("User wasn't found by id=1"));
        verify(userRepository).findById(1L);
        verify(userMapper,never()).mapToUserDto(user);
    }
    @Test
    void findAll() {
        List<User> userList = List.of(user);
        Pageable pageable = PageRequest.of(1, 3, Sort.by("id"));
        Page<User> page = new PageImpl<>(userList,pageable,1);
        Page<UserDtoResponse> expected = page.map(user1 -> modelMapper.map(user, UserDtoResponse.class));

        when(userRepository.findAll(pageable)).thenReturn(page);
        when(userMapper.mapToUserDtoResponse(user)).thenReturn(userDtoResponse);

        Page<UserDtoResponse> actual = userService.findAll(pageable);

        assertEquals(expected,actual);
        verify(userRepository).findAll(pageable);
        verify(userMapper).mapToUserDtoResponse(user);
    }

    @Test
    void save() {
        String incomingPassword = "artsiom";
        user.setRole(Role.ROLE_USER);
        user.setPassword(incomingPassword);
        UserDtoResponse expected = userDtoResponse;
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.existsByUsername(userDtoRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(incomingPassword)).thenReturn(incomingPassword);
        when(userMapper.mapToUser(1L,userDtoRequest, incomingPassword, user.getRole())).thenReturn(user);
        when(userMapper.mapToUserDtoResponse(user)).thenReturn(userDtoResponse);
        when(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME)).thenReturn(1L);


        UserDtoResponse actual = userService.save(userDtoRequest,incomingPassword);

        assertEquals(expected,actual);
        verify(userRepository).save(user);
        verify(passwordEncoder).encode(incomingPassword);
        verify(userRepository).existsByUsername(userDtoRequest.getUsername());
        verify(userMapper).mapToUser(1L,userDtoRequest, incomingPassword, user.getRole());
        verify(userMapper).mapToUserDtoResponse(user);
        verify(sequenceGeneratorService).generateSequence(User.SEQUENCE_NAME);
    }

    @Test
    void saveFail() {
        when(userRepository.existsByUsername(userDtoRequest.getUsername())).thenReturn(true);

        NotUniqueResourceException actual = assertThrows(NotUniqueResourceException.class,
                ()->userService.save(userDtoRequest,"somePassword"));

        assertTrue(actual.getMessage().contains("User already exists with username=Myachin"));
        verify(userRepository,never()).save(user);
        verify(userRepository).existsByUsername(userDtoRequest.getUsername());
        verify(userMapper,never()).mapToUser(1L,userDtoRequest, user.getPassword(), user.getRole());
        verify(userMapper,never()).mapToUserDtoResponse(user);
        verify(sequenceGeneratorService,never()).generateSequence(User.SEQUENCE_NAME);
    }

    @Test
    void update() {
        userDtoRequest.setDateOfBirth(LocalDate.now().minusYears(13));
        User changedUser = user;
        user.setDateOfBirth(LocalDate.now().minusYears(13));
        UserDtoResponse expected = userDtoResponse;
        expected.setDateOfBirth(LocalDate.now().minusYears(13));

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(userMapper.mapToUser(1L,userDtoRequest,user.getPassword(),user.getRole())).thenReturn(changedUser);
        when(userRepository.save(changedUser)).thenReturn(changedUser);
        when(userMapper.mapToUserDtoResponse(changedUser)).thenReturn(expected);

        UserDtoResponse actual = userService.update(1L, userDtoRequest,authenticatedUser);

        assertEquals(expected,actual);

        verify(userRepository).findById(1L);
        verify(userRepository,never()).existsByIdAndUsername(1L,userDtoRequest.getUsername());
        verify(userRepository,never()).existsByUsername(expected.getUsername());
        verify(userRepository).save(changedUser);
        verify(userMapper).mapToUser(1L,userDtoRequest,user.getPassword(),user.getRole());
        verify(userMapper).mapToUserDtoResponse(changedUser);
    }

    @Test
    void updateChangingName() {
        String newName = "yapaypayp";
        userDtoRequest.setUsername(newName);
        User changedUser = User.builder()
                .username("Myachin")
                .password("Artsiom")
                .email("myachinenergo@mail.ru")
                .id(1L)
                .dateOfBirth(LocalDate.now().minusYears(12))
                .role(Role.ROLE_ADMIN)
                .build();
        UserDtoResponse expected = userDtoResponse;
        expected.setUsername(newName);

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(userRepository.existsByUsername(newName)).thenReturn(false);
        when(userMapper.mapToUser(1L,userDtoRequest,user.getPassword(),user.getRole())).thenReturn(changedUser);
        when(userRepository.save(changedUser)).thenReturn(changedUser);
        when(userMapper.mapToUserDtoResponse(changedUser)).thenReturn(expected);

        UserDtoResponse actual = userService.update(1L, userDtoRequest,authenticatedUser);

        assertEquals(expected,actual);

        verify(userRepository).findById(1L);
        verify(userRepository).existsByUsername(newName);
        verify(userRepository).save(changedUser);
        verify(userMapper).mapToUser(1L,userDtoRequest,user.getPassword(),user.getRole());
        verify(userMapper).mapToUserDtoResponse(changedUser);
    }
    @Test
    void updateFailCredentials() {
        authenticatedUser = new AuthenticatedUser("dsa;da;dl", "some token", "ROLE_USER");
        when(userRepository.existsByIdAndUsername(1L, authenticatedUser.getUsername())).thenReturn(true);

        NotValidCredentialsException actual = assertThrows(NotValidCredentialsException.class,
                () -> userService.update(1L, userDtoRequest, authenticatedUser));

        assertTrue(actual.getMessage().contains("You have no rights to change this data"));
        verify(userRepository, never()).findById(1L);
        verify(userRepository).existsByIdAndUsername(1L, authenticatedUser.getUsername());
    }
    @Test
    void updateFailId() {
        authenticatedUser =new AuthenticatedUser("Myachin","someToken",Role.ROLE_USER.name());
        when(userRepository.existsByIdAndUsername(1L, authenticatedUser.getUsername())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> userService.update(1L, userDtoRequest, authenticatedUser));

        assertTrue(actual.getMessage().contains("User wasn't found by id=1"));
        verify(userRepository).findById(1L);
        verify(userRepository).existsByIdAndUsername(1L, authenticatedUser.getUsername());
    }
    @Test
    void updateFailUniqueUsername() {
        String changedUsername= "anotherName";
        userDtoRequest.setUsername(changedUsername);
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(userRepository.existsByUsername(userDtoRequest.getUsername())).thenReturn(true);

        NotUniqueResourceException actual = assertThrows(NotUniqueResourceException.class,
                () -> userService.update(1L, userDtoRequest, authenticatedUser));

        assertTrue(actual.getMessage().contains("User already exists with username="+changedUsername));
        verify(userRepository).findById(1L);
        verify(userRepository,never()).existsByIdAndUsername(1L, authenticatedUser.getUsername());
        verify(userRepository).existsByUsername(changedUsername);
    }
    @Test
    void deleteById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.mapToUserDtoResponse(user)).thenReturn(userDtoResponse);
        userService.deleteById(1L, authenticatedUser);

        verify(userRepository).findById(1L);
        verify(userRepository).deleteById(1L);
        verify(userMapper).mapToUserDtoResponse(user);
    }
    @Test
    void deleteByIdFail() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException actual= assertThrows(ResourceNotFoundException.class,
                ()->userService.deleteById(1L, authenticatedUser));

        assertTrue(actual.getMessage().contains("User wasn't found by id=1"));
        verify(userRepository).findById(1L);
        verify(userRepository,never()).deleteById(1L);
    }

//    @Test
//    void changePasswordByUserId() {
//    }

    @Test
    void loadUserByUsername() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority((user.getRole().name())));
        org.springframework.security.core.userdetails.User expected=
                new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),authorities);

        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(Optional.ofNullable(user));
        when(userMapper.mapToUserDto(user)).thenReturn(userDto);

        UserDetails actual = userService.loadUserByUsername(user.getUsername());

        assertEquals(expected,actual);
        verify(userRepository).findUserByUsername(user.getUsername());
        verify(userMapper).mapToUserDto(user);
    }

    @Test
    void loadUserByUsernameFail() {

        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(Optional.empty());

       ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
               ()->userService.loadUserByUsername(user.getUsername()));

        assertTrue(actual.getMessage().contains("User wasn't found by username="+user.getUsername()));
        verify(userRepository).findUserByUsername(user.getUsername());
    }
}
