package com.example.blogproject.service.impl;

import com.example.blogproject.mapper.CommentMapper;
import com.example.blogproject.model.Comment;
import com.example.blogproject.model.Post;
import com.example.blogproject.repository.CommentRepository;
import com.example.blogproject.service.CommentService;
import com.example.blogproject.service.PostService;
import com.example.blogproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;
    private final CommentMapper commentMapper;


    @Override
    public List<Comment> getCommentsByPost(Long postId) {
        log.info("Get all comments of post : {}",postId);
        return postService.getById(postId).getComments();
    }

    @Override
    @Transactional
    public Comment save(Comment comment, Long postId) {
        Post post = postService.getById(postId);
        Comment comment1 = commentRepository.save(comment);
        post.getComments().add(comment);
        postService.save(commentMapper.mapToPhoneDtoRequest(post));
        return comment1;
    }

    @Override
    @Transactional
    public Comment update(Long commentId, Long postId, Comment comment) {
        return postService.getById(postId).getComments().stream()
                .filter(comment1 -> comment1.getId().equals(comment.getId()))
                .findFirst()
                .map(comment1 -> commentRepository.save(comment))
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public void delete(Long commentId, Long postId) {
        Comment deletedComment = postService.getById(postId)
                .getComments().stream()
                .filter(comment -> Objects.equals(comment.getId(), commentId))
                .findAny()
                .orElseThrow(RuntimeException::new);
        commentRepository.delete(deletedComment);
    }
}
