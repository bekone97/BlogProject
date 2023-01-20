package com.example.blogproject.service;

import com.example.blogproject.dto.LoadFile;
import com.example.blogproject.dto.PostDtoRequest;
import com.example.blogproject.dto.PostDtoResponse;
import com.example.blogproject.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface PostService {

    PostDtoResponse getById(Long id);

    Page<PostDtoResponse> findAll(Pageable pageable);

    PostDtoResponse save(PostDtoRequest postDtoRequest, Principal principal);

    PostDtoResponse update(Long postId, PostDtoRequest postDtoRequest, Principal principal);

    void deleteById(Long postId, Principal principal);

    List<PostDtoResponse> findAllByUserId(Long userId);

    boolean existsById(Long postId);

    void addCommentToPost(Long postId, Comment newComment);

    boolean existsByPostIdAndComment(Long postId, Comment comment);

    PostDtoResponse addFileToPost(Long postId, MultipartFile file, Principal principal);

    PostDtoResponse editFileToPost(Long postId, MultipartFile file, Principal principal);

    void deleteFileToPost(Long postId, Principal principal);

    LoadFile getFileFromPost(Long postId);
}
