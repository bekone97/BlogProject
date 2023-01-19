package com.example.blogproject.service;

import com.example.blogproject.dto.CommentDtoRequest;
import com.example.blogproject.dto.CommentDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    Page<CommentDtoResponse> findAllCommentsByPost(Long postId, Pageable pageable);
    CommentDtoResponse findCommentByPostIdAndCommentId(Long postId, Long commentId);
    CommentDtoResponse save(CommentDtoRequest commentDtoRequest,Long postId);
    CommentDtoResponse update(Long commentId, Long postId, CommentDtoRequest commentDtoRequest);
    void delete(Long commentId, Long postId);
}
