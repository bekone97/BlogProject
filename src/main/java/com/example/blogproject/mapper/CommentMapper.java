package com.example.blogproject.mapper;

import com.example.blogproject.dto.PostDtoRequest;
import com.example.blogproject.model.Post;

public interface CommentMapper {
    PostDtoRequest mapToPhoneDtoRequest(Post post);
}
