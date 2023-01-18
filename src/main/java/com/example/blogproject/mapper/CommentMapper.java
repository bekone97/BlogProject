package com.example.blogproject.mapper;

import com.example.blogproject.dto.CommentDtoRequest;
import com.example.blogproject.dto.CommentDtoResponse;
import com.example.blogproject.dto.UserDtoResponse;
import com.example.blogproject.model.Comment;
import com.example.blogproject.model.User;

public interface CommentMapper {

    CommentDtoResponse mapToCommentDtoResponse(Comment comment);

    Comment mapToComment(Long commentId, UserDtoResponse userComment, CommentDtoRequest commentDtoRequest, Long postId);
    Comment mapToComment(Long commentId, User user, CommentDtoRequest commentDtoRequest);

}
