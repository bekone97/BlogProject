package com.example.blogproject.service;

import com.example.blogproject.model.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByPost(Long postId);
    Comment save(Comment comment,Long postId);
    Comment update(Long commentId, Long postId, Comment comment);
    void delete(Long commentId, Long postId);
}
