package com.example.blogservice.service;

import com.example.blogservice.dto.CommentDtoRequest;
import com.example.blogservice.dto.CommentDtoResponse;
import com.example.blogservice.model.Post;
import com.example.blogservice.model.User;
import com.example.blogservice.security.user.AuthenticatedUser;
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
