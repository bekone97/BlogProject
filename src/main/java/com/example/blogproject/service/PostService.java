package com.example.blogproject.service;

import com.example.blogproject.dto.PostDtoRequest;
import com.example.blogproject.dto.PostDtoResponse;
import com.example.blogproject.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {

    PostDtoResponse getById(Long id);

    Page<PostDtoResponse> findAll(Pageable pageable);

    PostDtoResponse save(PostDtoRequest postDtoRequest);

    PostDtoResponse update(Long postId,PostDtoRequest postDtoRequest);

    void deleteById(Long postId);

    List<PostDtoResponse> findAllByUserId(Long userId);

    boolean existsById(Long postId);

    void addCommentToPost(Long postId, Comment newComment);

    boolean existsByPostIdAndComment(Long postId, Comment comment);

}
