package com.example.blogproject.mapper;

import com.example.blogproject.dto.PostDtoRequest;
import com.example.blogproject.dto.PostDtoResponse;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.model.Comment;
import com.example.blogproject.model.Post;

import java.util.List;

public interface PostMapper {
    Post mapToPost(Long postId, PostDtoRequest postDtoRequest, UserDtoResponse userDtoResponse);

    PostDtoResponse mapToPostDtoResponse(Post post);

    Post mapToPost(Long postId, PostDtoRequest postDtoRequest, UserDtoResponse userDtoResponse, List<Comment> comments);
}
