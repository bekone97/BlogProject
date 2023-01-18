package com.example.blogproject.service;

import com.example.blogproject.dto.CommentDtoRequest;
import com.example.blogproject.dto.CommentDtoResponse;
import com.example.blogproject.model.Comment;

import java.util.List;

public interface CommentService {
    List<CommentDtoResponse> findAllCommentsByPost(Long postId);
    CommentDtoResponse findCommentByPostIdAndCommentId(Long postId, Long commentId);
    CommentDtoResponse save(CommentDtoRequest commentDtoRequest,Long postId);
    CommentDtoResponse update(Long commentId, Long postId, CommentDtoRequest commentDtoRequest);
    void delete(Long commentId, Long postId);
}
