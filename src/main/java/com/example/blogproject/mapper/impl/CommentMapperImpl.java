package com.example.blogproject.mapper.impl;

import com.example.blogproject.dto.PostDtoRequest;
import com.example.blogproject.mapper.CommentMapper;
import com.example.blogproject.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentMapperImpl implements CommentMapper {
    @Override
    public PostDtoRequest mapToPhoneDtoRequest(Post post) {
        return null;
    }
}
