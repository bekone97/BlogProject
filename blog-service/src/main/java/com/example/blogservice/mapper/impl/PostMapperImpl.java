package com.example.blogservice.mapper.impl;

import com.example.blogservice.dto.PostDtoRequest;
import com.example.blogservice.dto.PostDtoResponse;
import com.example.blogservice.dto.UserDtoResponse;
import com.example.blogservice.mapper.CommentMapper;
import com.example.blogservice.mapper.PostMapper;
import com.example.blogservice.mapper.UserMapper;
import com.example.blogservice.model.Comment;
import com.example.blogservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostMapperImpl implements PostMapper {
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;

    @Override
    public Post mapToPost(Long postId, PostDtoRequest postDtoRequest, UserDtoResponse userDtoResponse) {
        Post post = modelMapper.map(postDtoRequest, Post.class);
        post.setUser(userMapper.mapToUser(userDtoResponse));
        post.setId(postId);
        post.setComments(new ArrayList<>());
        return post;
    }

    @Override
    public PostDtoResponse mapToPostDtoResponse(Post post) {
        PostDtoResponse postDtoResponse = modelMapper.map(post, PostDtoResponse.class);
        postDtoResponse.setUserDtoResponse(userMapper.mapToUserDtoResponse(post.getUser()));
        postDtoResponse.setComments(post.getComments() == null || post.getComments().isEmpty() ?
                new ArrayList<>() :
                post.getComments().stream()
                        .map(commentMapper::mapToCommentDtoResponse)
                        .collect(Collectors.toList()));
        return postDtoResponse;
    }

    @Override
    public Post mapToPost(Long postId, PostDtoRequest postDtoRequest, UserDtoResponse userDtoResponse, List<Comment> comments) {
        Post post = modelMapper.map(postDtoRequest, Post.class);
        post.setUser(userMapper.mapToUser(userDtoResponse));
        post.setId(postId);
        post.setComments(comments);
        return post;
    }
}
