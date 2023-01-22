package com.example.blogproject.service;

import com.example.blogproject.dto.LoadFile;
import com.example.blogproject.dto.PostDtoRequest;
import com.example.blogproject.dto.PostDtoResponse;
import com.example.blogproject.model.Comment;
import com.example.blogproject.model.User;
import com.example.blogproject.security.user.AuthenticatedUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    PostDtoResponse getById(Long id);

    Page<PostDtoResponse> findAll(Pageable pageable);

    PostDtoResponse save(PostDtoRequest postDtoRequest, AuthenticatedUser authenticatedUser);

    PostDtoResponse update(Long postId, PostDtoRequest postDtoRequest, AuthenticatedUser principal);

    void deleteById(Long postId, AuthenticatedUser authenticatedUser);

    List<PostDtoResponse> findAllByUserId(Long userId);

    boolean existsById(Long postId);

    void addCommentToPost(Long postId, Comment newComment);

    boolean existsByPostIdAndComment(Long postId, Comment comment);

    PostDtoResponse addFileToPost(Long postId, MultipartFile file, AuthenticatedUser authenticatedUser);

    PostDtoResponse editFileToPost(Long postId, MultipartFile file, AuthenticatedUser authenticatedUser);

    void deleteFileToPost(Long postId, AuthenticatedUser authenticatedUser);

    LoadFile getFileFromPost(Long postId);

    void deleteAllByUser(User user);

    void deleteCommentFromPostByComment(Comment comment);
}
