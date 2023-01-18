package com.example.blogproject.controller;

import com.example.blogproject.dto.UserDtoRequest;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(OK)
    public List<UserDtoResponse> getUsers(){
        log.info("Get all users");
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    @ResponseStatus(OK)
    public UserDtoResponse getUserById(@PathVariable Long userId){
        log.info("Get user by id : {}",userId);
        return userService.getById(userId);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public UserDtoResponse save(@RequestBody UserDtoRequest userDtoRequest){
        log.info("Save user by : {}",userDtoRequest);
        return userService.save(userDtoRequest);
    }

    @PutMapping("/{userId}")
    @ResponseStatus(OK)
    public UserDtoResponse update(@PathVariable Long userId,
                                  @RequestBody UserDtoRequest userDtoRequest){
        log.info("Update user with id : {} by : {}",userId,userDtoRequest);
        return userService.update(userId,userDtoRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(OK)
    public void deleteById(@PathVariable Long userId){
        log.info("Delete user by id");
        userService.deleteById(userId);
    }
}
