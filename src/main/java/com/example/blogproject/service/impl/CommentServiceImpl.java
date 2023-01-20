package com.example.blogproject.service.impl;

import com.example.blogproject.dto.CommentDtoRequest;
import com.example.blogproject.dto.CommentDtoResponse;
import com.example.blogproject.dto.PostDtoResponse;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.exception.ResourceNotFoundException;
import com.example.blogproject.mapper.CommentMapper;
import com.example.blogproject.model.Comment;
import com.example.blogproject.model.Post;
import com.example.blogproject.repository.CommentRepository;
import com.example.blogproject.security.user.AuthenticatedUser;
import com.example.blogproject.service.CommentService;
import com.example.blogproject.service.PostService;
import com.example.blogproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;
    private final CommentMapper commentMapper;
    private final SequenceGeneratorService sequenceGeneratorService;


    @Override
    public Page<CommentDtoResponse> findAllCommentsByPost(Long postId, Pageable pageable) {
        log.info("Get all comments of post : {}",postId);
        PostDtoResponse postDtoResponse=postService.getById(postId);
        List<Long> commentIds = postDtoResponse.getComments().stream()
                .map(CommentDtoResponse::getId)
                .collect(Collectors.toList());
        return commentRepository.findAllByIdIn(commentIds,pageable)
                .map(commentMapper::mapToCommentDtoResponse);
    }

    @Override
    public CommentDtoResponse findCommentByPostIdAndCommentId(Long postId, Long commentId) {
        log.info("Get comment with id : {} from post with id : {}",commentId,postId);
        return postService.getById(postId).getComments().stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findFirst()
                .orElseThrow(()->new ResourceNotFoundException(Comment.class,"id",commentId,Post.class,"id",postId));
    }

    @Override
    @Transactional
    public CommentDtoResponse save(CommentDtoRequest commentDtoRequest, Long postId, Principal principal) {
        log.info("Save new comment by : {} to post with id : {}",commentDtoRequest,postId);
        if (postService.existsById(postId)) {
            UserDtoResponse userComment = userService.getById(commentDtoRequest.getUserId());
            Comment newComment = commentMapper.mapToComment(sequenceGeneratorService.generateSequence(
                    Comment.SEQUENCE_NAME),userComment,commentDtoRequest,postId );
            newComment=commentRepository.save(newComment);
            postService.addCommentToPost(postId,newComment);
            return commentMapper.mapToCommentDtoResponse(newComment);
        }
        throw new ResourceNotFoundException(Post.class,"id",postId);
    }

    @Override
    @Transactional
    public CommentDtoResponse update(Long commentId, Long postId, CommentDtoRequest commentDtoRequest, Principal principal) {
        log.info("Update comment with id : {} by : {} from post with id : {}",commentId,commentDtoRequest,postId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->{
                    log.error("Comment with id : {} wasn't found",commentId);
                    return  new ResourceNotFoundException(Comment.class,"id",commentId);
                });
        checkValidCredentials(postId,commentId, principal);
        if (!postService.existsByPostIdAndComment(postId,comment)){
            throw new ResourceNotFoundException(Comment.class,"id",commentId,Post.class,"id",postId);
        }
        comment = commentMapper.mapToComment(comment.getId(),comment.getUser(),commentDtoRequest);
        return commentMapper.mapToCommentDtoResponse(commentRepository.save(comment));
    }

    @Override
    public void delete(Long commentId, Long postId, Principal principal) {
        log.info("Delete comment by id : {}  from post with id : {}", commentId,postId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->{
                    log.error("Comment with id : {} wasn't found",commentId);
                    return  new ResourceNotFoundException(Comment.class,"id",commentId);
                });
        checkValidCredentials(postId,commentId,principal);
        if (!postService.existsByPostIdAndComment(postId,comment)){
            throw new ResourceNotFoundException(Comment.class, "id", commentId, Post.class, "id", postId);
        }
        commentRepository.delete(comment);
        
    }


    private void checkValidCredentials(Long postId, Long commentId, Principal principal) {
        AuthenticatedUser currentUser = (AuthenticatedUser) principal;
        PostDtoResponse postDtoResponse = postService.getById(postId);
        boolean isValid = postDtoResponse.getComments().stream()
                .filter(comment->comment.getId().equals(commentId))
                .findFirst()
                .map(comment-> comment.getUserDtoResponse().getUsername().equals(currentUser.getUsername()) ||
                        currentUser.getAuthorities().stream().anyMatch(authority->authority.getAuthority().equals("ROLE_ADMIN")))
                .orElseThrow(RuntimeException::new);
        if (!isValid)
            throw new RuntimeException();
    }
}
