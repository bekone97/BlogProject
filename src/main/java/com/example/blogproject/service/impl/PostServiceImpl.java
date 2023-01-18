package com.example.blogproject.service.impl;

import com.example.blogproject.dto.PostDtoRequest;
import com.example.blogproject.dto.PostDtoResponse;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.exception.ResourceNotFoundException;
import com.example.blogproject.mapper.PostMapper;
import com.example.blogproject.model.Comment;
import com.example.blogproject.model.Post;
import com.example.blogproject.model.User;
import com.example.blogproject.repository.PostRepository;
import com.example.blogproject.service.PostService;
import com.example.blogproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserService userService;
    private final SequenceGeneratorService sequenceGeneratorService;

    @Override
    public PostDtoResponse getById(Long id) {
        log.info("Get post by id : {}",id);
        return postRepository.findById(id)
                .map(postMapper::mapToPostDtoResponse)
                .orElseThrow(()-> {
                    log.error("Post wasn't found by id : {}",id);
                    return new ResourceNotFoundException(Post.class,"id",id);
                });
    }

    @Override
    public List<PostDtoResponse> findAll(){
        log.info("Find all posts");
        return postRepository.findAll().stream()
                .map(postMapper::mapToPostDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostDtoResponse save(PostDtoRequest postDtoRequest) {
        log.info("Save post from postDroRequest:{}",postDtoRequest);
        UserDtoResponse userDtoResponse = userService.getById(postDtoRequest.getUserId());
        Post post = postMapper.mapToPost(sequenceGeneratorService.generateSequence(Post.SEQUENCE_NAME),
                postDtoRequest,userDtoResponse);
        return postMapper.mapToPostDtoResponse(postRepository.save(post));
    }

    @Override
    @Transactional
    public PostDtoResponse update(Long postId, PostDtoRequest postDtoRequest) {
        log.info("Check existing post by id : {} and update it by : {}",postId,postDtoRequest);
        UserDtoResponse userDtoResponse = userService.getById(postDtoRequest.getUserId());
        return postRepository.findById(postId)
                .map(post -> postMapper.mapToPost(postId,postDtoRequest,userDtoResponse,post.getComments()))
                .map(postRepository::save)
                .map(postMapper::mapToPostDtoResponse)
                .orElseThrow(()->{
                    log.error("Post wasn't find by id : {}", postId);
                    return new ResourceNotFoundException(Post.class,"id",postId);
                });
    }

    @Override
    @Transactional
    public void deleteById(Long postId) {
        log.info("Check existing post by id : {} and delete it",postId);
        PostDtoResponse byId = getById(postId);
        postRepository.deleteById(postId);
    }

    @Override
    public List<PostDtoResponse> findAllByUserId(Long userId) {
        log.info("Find all posts by user id : {}",userId);
        if (userService.existsById(userId)){
            return postRepository.findAllByUserId(userId).stream()
                    .map(postMapper::mapToPostDtoResponse)
                    .collect(Collectors.toList());
        }
        throw new ResourceNotFoundException(User.class,"id",userId);
    }

    @Override
    public boolean existsById(Long postId) {
        log.info("Check existing by id : {} ",postId);
        return postRepository.existsById(postId);
    }

    @Override
    @Transactional
    public void addCommentToPost(Long postId, Comment newComment) {
        Post savedPost = postRepository.findById(postId)
                .map(post -> {
                    if (post.getComments()==null){
                        post.setComments(List.of(newComment));
                    }else {
                        post.getComments().add(newComment);
                    }
                    return postRepository.save(post);
                })
                .orElseThrow(() -> new ResourceNotFoundException(Post.class, "id", postId));
    }

    @Override
    public boolean existsByPostIdAndComment(Long postId, Comment comment) {
        return postRepository.existsByIdAndCommentsContaining(postId,comment);
    }

}
