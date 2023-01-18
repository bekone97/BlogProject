package com.example.blogproject.mapper.impl;

import com.example.blogproject.dto.PostDtoRequest;
import com.example.blogproject.dto.PostDtoResponse;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.mapper.CommentMapper;
import com.example.blogproject.mapper.PostMapper;
import com.example.blogproject.mapper.UserMapper;
import com.example.blogproject.model.Comment;
import com.example.blogproject.model.Post;
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
        postDtoResponse.setComments(post.getComments()==null?
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
