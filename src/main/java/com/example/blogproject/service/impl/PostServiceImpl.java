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
import com.example.blogproject.security.user.AuthenticatedUser;
import com.example.blogproject.service.FileService;
import com.example.blogproject.service.PostService;
import com.example.blogproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
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
    private final FileService fileService;

    @Override
    public PostDtoResponse getById(Long id) {
        log.info("Get post by id : {}",id);
        return postRepository.findById(id)
                .map(this::getPostDtoResponse)
                .orElseThrow(()-> {
                    log.error("Post wasn't found by id : {}",id);
                    return new ResourceNotFoundException(Post.class,"id",id);
                });
    }



    @Override
    public Page<PostDtoResponse> findAll(Pageable pageable) {
        log.info("Find all posts");
        return postRepository.findAll(pageable != null ?
                        pageable :
                        PageRequest.of(1, 3, Sort.by("id")))
                .map((this::getPostDtoResponse));
    }

    @Override
    @Transactional
    public PostDtoResponse save(PostDtoRequest postDtoRequest, Principal principal) {
        log.info("Save post from postDroRequest:{}",postDtoRequest);

        UserDtoResponse userDtoResponse = userService.getById(postDtoRequest.getUserId());
        Post post = getPostFromRequest(postDtoRequest, userDtoResponse);
        post=postRepository.save(post);
        return getPostDtoResponse(post);
    }

    private Post getPostFromRequest(PostDtoRequest postDtoRequest, UserDtoResponse userDtoResponse) {
        Post post = postMapper.mapToPost(sequenceGeneratorService.generateSequence(Post.SEQUENCE_NAME),
                postDtoRequest, userDtoResponse);
        if (postDtoRequest.getFile()!=null)
            post.setFile(fileService.uploadFile(postDtoRequest.getFile()));
        return post;
    }
    private Post getPostFromRequest(Long postId,PostDtoRequest postDtoRequest, UserDtoResponse userDtoResponse,
                                    List<Comment> comments) {
        Post post = postMapper.mapToPost(postId,
                postDtoRequest, userDtoResponse,comments);
        if (postDtoRequest.getFile()!=null)
            post.setFile(fileService.uploadFile(postDtoRequest.getFile()));
        return post;
    }

    @Override
    @Transactional
    public PostDtoResponse update(Long postId, PostDtoRequest postDtoRequest, Principal principal) {
        log.info("Check existing post by id : {} and update it by : {}",postId,postDtoRequest);

        UserDtoResponse userDtoResponse = userService.getById(postDtoRequest.getUserId());
        checkValidCredentials(userDtoResponse,principal);
        return postRepository.findById(postId)
                .map(post ->getPostFromRequest(postId,postDtoRequest,userDtoResponse,post.getComments()))
                .map(postRepository::save)
                .map(this::getPostDtoResponse)
                .orElseThrow(()->{
                    log.error("Post wasn't find by id : {}", postId);
                    return new ResourceNotFoundException(Post.class,"id",postId);
                });
    }

    @Override
    @Transactional
    public void deleteById(Long postId, Principal principal) {
        log.info("Check existing post by id : {} and delete it",postId);
        PostDtoResponse byId = getById(postId);
        checkValidCredentials(byId.getUserDtoResponse(), principal);
        postRepository.deleteById(postId);
    }

    @Override
    public List<PostDtoResponse> findAllByUserId(Long userId) {
        log.info("Find all posts by user id : {}",userId);
        if (userService.existsById(userId)){
            return postRepository.findAllByUserId(userId).stream()
                    .map(this::getPostDtoResponse)
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
       postRepository.findById(postId)
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

    private PostDtoResponse getPostDtoResponse(Post post) {
        PostDtoResponse postDtoResponse = postMapper.mapToPostDtoResponse(post);
        if (post.getFile()!=null)
            postDtoResponse.setFile(fileService.downloadFile(post.getFile()));
        return postDtoResponse;
    }

    private void checkValidCredentials(UserDtoResponse userDtoResponse, Principal principal) {
        AuthenticatedUser currentUser = (AuthenticatedUser) principal;
        if (!userDtoResponse.getUsername().equals(currentUser.getUsername())||
                currentUser.getAuthorities().stream().noneMatch(authority->authority.getAuthority().equals("ROLE_ADMIN"))){
            throw new RuntimeException();
        }
    }

}
