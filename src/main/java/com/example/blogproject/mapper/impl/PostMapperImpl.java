package com.example.blogproject.mapper.impl;

import com.example.blogproject.dto.PostDtoRequest;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.mapper.PostMapper;
import com.example.blogproject.mapper.UserMapper;
import com.example.blogproject.model.Post;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapperImpl implements PostMapper {
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    @Override
    public Post mapToPost(PostDtoRequest postDtoRequest, UserDtoResponse userDtoResponse) {
        Post post = modelMapper.map(postDtoRequest,Post.class);
        post.setUser(userMapper.mapToUser(userDtoResponse));
        return post;
    }
}
