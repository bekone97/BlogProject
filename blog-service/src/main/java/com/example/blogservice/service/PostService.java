package com.example.blogservice.service;

import com.example.blogservice.dto.LoadFile;
import com.example.blogservice.dto.PostDtoRequest;
import com.example.blogservice.dto.PostDtoResponse;
import com.example.blogservice.model.Comment;
import com.example.blogservice.model.User;
import com.example.blogservice.security.user.AuthenticatedUser;
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

    PostDtoResponse replaceFileInPost(Long postId, MultipartFile file, AuthenticatedUser authenticatedUser);

    void deleteFileInPost(Long postId, AuthenticatedUser authenticatedUser);

    LoadFile getFileFromPost(Long postId);

    void deleteAllByUser(User user);

    void deleteCommentFromPostByComment(Comment comment);
}
