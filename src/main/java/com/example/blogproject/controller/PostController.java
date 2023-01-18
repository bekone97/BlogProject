package com.example.blogproject.controller;

import com.example.blogproject.dto.PostDtoRequest;
import com.example.blogproject.model.Post;
import com.example.blogproject.service.PostService;
import com.example.blogproject.service.impl.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;

    @GetMapping
    @ResponseStatus(OK)
    public List<Post> findAllPosts(){
        log.info("Find all posts");
        return postService.findAll();
    }

    @GetMapping("/{postId}")
    @ResponseStatus(OK)
    public Post getPostById(@PathVariable Long postId){
        log.info("Get post by id:{}",postId);
        return postService.getById(postId);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Post savePost(@RequestBody PostDtoRequest postDtoRequest){
        log.info("Save postDtoRequest : {}",postDtoRequest);
        return postService.save(postDtoRequest);
    }
}
