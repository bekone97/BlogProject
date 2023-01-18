package com.example.blogproject.service;

import com.example.blogproject.dto.PostDtoRequest;
import com.example.blogproject.dto.PostDtoResponse;

import java.util.List;

public interface PostService {

    PostDtoResponse getById(Long id);

    List<PostDtoResponse> findAll();

    PostDtoResponse save(PostDtoRequest postDtoRequest);

    PostDtoResponse update(Long postId,PostDtoRequest postDtoRequest);

    void deleteById(Long postId);

    List<PostDtoResponse> findAllByUserId(Long userId);
}
