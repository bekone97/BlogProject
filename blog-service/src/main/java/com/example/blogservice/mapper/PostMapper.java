package com.example.blogservice.mapper;

import com.example.blogservice.dto.PostDtoRequest;
import com.example.blogservice.dto.PostDtoResponse;
import com.example.blogservice.dto.UserDtoResponse;
import com.example.blogservice.model.Comment;
import com.example.blogservice.model.Post;

import java.util.List;

public interface PostMapper {
    Post mapToPost(Long postId, PostDtoRequest postDtoRequest, UserDtoResponse userDtoResponse);

    PostDtoResponse mapToPostDtoResponse(Post post);

    Post mapToPost(Long postId, PostDtoRequest postDtoRequest, UserDtoResponse userDtoResponse, List<Comment> comments);
}
