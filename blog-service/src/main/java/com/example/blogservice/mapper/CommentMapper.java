package com.example.blogservice.mapper;

import com.example.blogservice.dto.CommentDtoRequest;
import com.example.blogservice.dto.CommentDtoResponse;
import com.example.blogservice.dto.UserDtoResponse;
import com.example.blogservice.model.Comment;
import com.example.blogservice.model.User;

public interface CommentMapper {

    CommentDtoResponse mapToCommentDtoResponse(Comment comment);

    Comment mapToComment(Long commentId, UserDtoResponse userComment, CommentDtoRequest commentDtoRequest);
    Comment mapToComment(Long commentId, User user, CommentDtoRequest commentDtoRequest);

}
