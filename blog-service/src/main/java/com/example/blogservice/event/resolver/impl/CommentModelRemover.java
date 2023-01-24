package com.example.blogservice.event.resolver.impl;

import com.example.blogservice.event.ModelType;
import com.example.blogservice.event.resolver.ModelRemover;
import com.example.blogservice.model.Comment;
import com.example.blogservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentModelRemover implements ModelRemover {
    private final PostService postService;

    @Override
    public ModelType getModelType() {
        return ModelType.COMMENT;
    }

    @Override
    @Transactional
    public void prepareModelRemoving(Object model) {
        log.info("Delete comment from post with comment id : {}", model);
        Comment comment = (Comment) model;
        postService.deleteCommentFromPostByComment(comment);
    }
}
