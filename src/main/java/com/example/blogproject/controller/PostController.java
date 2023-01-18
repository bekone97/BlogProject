package com.example.blogproject.controller;

import com.example.blogproject.dto.PostDtoRequest;
import com.example.blogproject.dto.PostDtoResponse;
import com.example.blogproject.service.PostService;
import com.example.blogproject.validator.ValidId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PostController {

    private final PostService postService;

    @GetMapping
    @ResponseStatus(OK)
    public List<PostDtoResponse> findAllPosts(){
        log.info("Find all posts");
        return postService.findAll();
    }

    @GetMapping("/{postId}")
    @ResponseStatus(OK)
    public PostDtoResponse getPostById(@PathVariable @ValidId Long postId){
        log.info("Get post by id:{}",postId);
        return postService.getById(postId);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public PostDtoResponse save(@Valid @RequestBody PostDtoRequest postDtoRequest){
        log.info("Save new post by : {}",postDtoRequest);
        return postService.save(postDtoRequest);
    }

    @PutMapping("/{postId}")
    @ResponseStatus(OK)
    public PostDtoResponse update(@PathVariable @ValidId Long postId,
                                  @Valid @RequestBody PostDtoRequest postDtoRequest){
        log.info("Update post by id : {} and by : {}",postId,postDtoRequest);
        return postService.update(postId,postDtoRequest);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(OK)
    public void deleteById(@PathVariable @ValidId Long postId){
        log.info("Delete post by id: {}",postId);
        postService.deleteById(postId);
    }

    @GetMapping("/byUser/{userId}")
    @ResponseStatus(OK)
    public List<PostDtoResponse> findAllResponseByUserId(@PathVariable @ValidId Long userId){
        log.info("Find all posts by user id : {}",userId);
        return postService.findAllByUserId(userId);
    }
}
