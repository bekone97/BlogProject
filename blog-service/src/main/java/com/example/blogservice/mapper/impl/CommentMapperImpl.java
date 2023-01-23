package com.example.blogservice.mapper.impl;

import com.example.blogservice.dto.CommentDtoRequest;
import com.example.blogservice.dto.CommentDtoResponse;
import com.example.blogservice.dto.UserDtoResponse;
import com.example.blogservice.mapper.CommentMapper;
import com.example.blogservice.mapper.UserMapper;
import com.example.blogservice.model.Comment;
import com.example.blogservice.model.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentMapperImpl implements CommentMapper {

    private final ModelMapper modelMapper;
    private final UserMapper userMapper;

    @Override
    public CommentDtoResponse mapToCommentDtoResponse(Comment comment) {
        CommentDtoResponse commentDtoResponse = modelMapper.map(comment, CommentDtoResponse.class);
        commentDtoResponse.setUserDtoResponse(userMapper.mapToUserDtoResponse(comment.getUser()));
        return commentDtoResponse;
    }

    @Override
    public Comment mapToComment(Long commentId,
                                UserDtoResponse userComment,
                                CommentDtoRequest commentDtoRequest, Long postId) {
        Comment comment = modelMapper.map(commentDtoRequest,Comment.class);
        comment.setUser(userMapper.mapToUser(userComment));
        comment.setId(commentId);
        return comment;
    }

    @Override
    public Comment mapToComment(Long commentId, User user, CommentDtoRequest commentDtoRequest) {
        Comment comment = modelMapper.map(commentDtoRequest,Comment.class);
        comment.setUser(user);
        comment.setId(commentId);
        return comment;
    }
}
