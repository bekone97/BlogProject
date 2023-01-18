package com.example.blogproject.mapper.impl;

import com.example.blogproject.dto.CommentDtoRequest;
import com.example.blogproject.dto.CommentDtoResponse;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.mapper.CommentMapper;
import com.example.blogproject.mapper.UserMapper;
import com.example.blogproject.model.Comment;
import com.example.blogproject.model.User;
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
