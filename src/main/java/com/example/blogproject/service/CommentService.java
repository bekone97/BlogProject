package com.example.blogproject.service;

import com.example.blogproject.dto.CommentDtoRequest;
import com.example.blogproject.dto.CommentDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;

public interface CommentService {
    Page<CommentDtoResponse> findAllCommentsByPost(Long postId, Pageable pageable);
    CommentDtoResponse findCommentByPostIdAndCommentId(Long postId, Long commentId);
    CommentDtoResponse save(CommentDtoRequest commentDtoRequest, Long postId, Principal principal);
    CommentDtoResponse update(Long commentId, Long postId, CommentDtoRequest commentDtoRequest, Principal principal);
    void delete(Long commentId, Long postId, Principal principal);
}
