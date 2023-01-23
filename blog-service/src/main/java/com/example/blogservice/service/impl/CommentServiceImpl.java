package com.example.blogservice.service.impl;

import com.example.blogservice.dto.CommentDtoRequest;
import com.example.blogservice.dto.CommentDtoResponse;
import com.example.blogservice.dto.PostDtoResponse;
import com.example.blogservice.dto.UserDtoResponse;
import com.example.blogservice.event.ModelCreatedEvent;
import com.example.blogservice.event.ModelDeletedEvent;
import com.example.blogservice.event.ModelType;
import com.example.blogservice.event.ModelUpdatedEvent;
import com.example.blogservice.exception.ResourceNotFoundException;
import com.example.blogservice.mapper.CommentMapper;
import com.example.blogservice.model.Comment;
import com.example.blogservice.model.Post;
import com.example.blogservice.model.User;
import com.example.blogservice.repository.CommentRepository;
import com.example.blogservice.security.user.AuthenticatedUser;
import com.example.blogservice.service.CommentService;
import com.example.blogservice.service.PostService;
import com.example.blogservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ApplicationEventPublisher applicationEventPublisher;

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
    @Cacheable(value = "comment",key = "#commentId")
    public CommentDtoResponse findCommentByPostIdAndCommentId(Long postId, Long commentId) {
        log.info("Get comment with id : {} from post with id : {}",commentId,postId);
        return postService.getById(postId).getComments().stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findFirst()
                .orElseThrow(()->new ResourceNotFoundException(Comment.class,"id",commentId,Post.class,"id",postId));
    }

    @Override
    @Transactional
    public CommentDtoResponse save(CommentDtoRequest commentDtoRequest, Long postId, AuthenticatedUser authenticatedUser) {
        log.info("Save new comment by : {} to post with id : {}",commentDtoRequest,postId);

        if (postService.existsById(postId)) {
            UserDtoResponse userComment = userService.getById(commentDtoRequest.getUserId());
            Comment newComment = commentMapper.mapToComment(sequenceGeneratorService.generateSequence(
                    Comment.SEQUENCE_NAME),userComment,commentDtoRequest,postId );
            newComment=commentRepository.save(newComment);
            postService.addCommentToPost(postId,newComment);
            publishSave(newComment);
            return commentMapper.mapToCommentDtoResponse(newComment);
        }
        throw new ResourceNotFoundException(Post.class,"id",postId);
    }

    private void publishSave(Comment newComment) {
        applicationEventPublisher.publishEvent(ModelCreatedEvent.builder()
                        .modelName(Comment.class.getName())
                        .modelId(newComment.getId())
                .build());
    }

    @Override
    @Transactional
    @CachePut(value = "comment",key = "#commentId")
    public CommentDtoResponse update(Long commentId, Long postId, CommentDtoRequest commentDtoRequest, AuthenticatedUser authenticatedUser) {
        log.info("Update comment with id : {} by : {} from post with id : {}",commentId,commentDtoRequest,postId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->{
                    log.error("Comment with id : {} wasn't found",commentId);
                    return  new ResourceNotFoundException(Comment.class,"id",commentId);
                });
        checkValidCredentials(postId,commentId, authenticatedUser);
        if (!postService.existsByPostIdAndComment(postId,comment)){
            throw new ResourceNotFoundException(Comment.class,"id",commentId,Post.class,"id",postId);
        }
        comment = commentMapper.mapToComment(comment.getId(),comment.getUser(),commentDtoRequest);
        return commentMapper.mapToCommentDtoResponse(commentRepository.save(comment));
    }



    @Override
    @CacheEvict(value = "comment",key = "#commentId")
    public void delete(Long commentId, Long postId, AuthenticatedUser authenticatedUser) {
        log.info("Delete comment by id : {}  from post with id : {}", commentId,postId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->{
                    log.error("Comment with id : {} wasn't found",commentId);
                    return  new ResourceNotFoundException(Comment.class,"id",commentId);
                });
        checkValidCredentials(postId,commentId, authenticatedUser);
        if (!postService.existsByPostIdAndComment(postId,comment)){
            throw new ResourceNotFoundException(Comment.class, "id", commentId, Post.class, "id", postId);
        }
        publishDelete(comment);
        commentRepository.delete(comment);
        
    }

    private void publishDelete(Comment comment) {
        applicationEventPublisher.publishEvent(ModelDeletedEvent.builder()
                        .model(comment)
                        .modelType(ModelType.COMMENT)
                .build());
    }
    private void publishUpdate(Long commentId) {
        applicationEventPublisher.publishEvent(ModelUpdatedEvent.builder()
                .modelName(Comment.class.getName())
                .modelId(commentId)
                .build());
    }
    @Override
    public void deleteAllByUser(User user) {
        log.info("Delete all coments by user");
        commentRepository.findAllByUserId(user.getId()).stream()
                .peek(this::publishDelete)
                .forEachOrdered(commentRepository::delete);
    }

    @Override
    public void deleteAllByPost(Post post) {
        log.info("Delete all comments by post : {}",post);
        commentRepository.deleteAll(post.getComments());
    }


    private void checkValidCredentials(Long postId, Long commentId, AuthenticatedUser authenticatedUser) {
        PostDtoResponse postDtoResponse = postService.getById(postId);
        boolean isValid = postDtoResponse.getComments().stream()
                .filter(comment->comment.getId().equals(commentId))
                .findFirst()
                .map(comment-> comment.getUserDtoResponse().getUsername().equals(authenticatedUser.getUsername()) ||
                        authenticatedUser.getAuthorities().stream().anyMatch(authority->authority.getAuthority().equals("ROLE_ADMIN")))
                .orElseThrow(RuntimeException::new);
        if (!isValid)
            throw new RuntimeException();
    }
}
