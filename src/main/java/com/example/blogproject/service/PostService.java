package com.example.blogproject.service;

import com.example.blogproject.dto.PostDtoRequest;
import com.example.blogproject.model.Post;

import java.util.List;

public interface PostService {

    Post getById(Long id);

    List<Post> findAll();

    Post save(PostDtoRequest postDtoRequest);
}
