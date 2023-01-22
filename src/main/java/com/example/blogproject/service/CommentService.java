package com.example.blogproject.service;

import com.example.blogproject.dto.CommentDtoRequest;
import com.example.blogproject.dto.CommentDtoResponse;
import com.example.blogproject.model.Post;
import com.example.blogproject.model.User;
import com.example.blogproject.security.user.AuthenticatedUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    Page<CommentDtoResponse> findAllCommentsByPost(Long postId, Pageable pageable);
    CommentDtoResponse findCommentByPostIdAndCommentId(Long postId, Long commentId);
    CommentDtoResponse save(CommentDtoRequest commentDtoRequest, Long postId, AuthenticatedUser principal);
    CommentDtoResponse update(Long commentId, Long postId, CommentDtoRequest commentDtoRequest, AuthenticatedUser authenticatedUser);
    void delete(Long commentId, Long postId, AuthenticatedUser authenticatedUser);
    void deleteAllByUser(User user);
    void deleteAllByPost(Post post);
}
