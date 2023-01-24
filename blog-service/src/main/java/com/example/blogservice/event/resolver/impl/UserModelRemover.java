package com.example.blogservice.event.resolver.impl;

import com.example.blogservice.event.ModelType;
import com.example.blogservice.event.resolver.ModelRemover;
import com.example.blogservice.model.User;
import com.example.blogservice.service.CommentService;
import com.example.blogservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserModelRemover implements ModelRemover {

    private final CommentService commentService;
    private final PostService postService;

    @Override
    public ModelType getModelType() {
        return ModelType.USER;
    }

    @Override
    @Transactional
    public void prepareModelRemoving(Object model) {
        log.info("Delete all user's posts and comments  with user id : {}", model);
        User user = (User) model;
        postService.deleteAllByUser(user);
        commentService.deleteAllByUser(user);
    }
}
