package com.example.blogservice.event.resolver.impl;

import com.example.blogservice.event.ModelType;
import com.example.blogservice.event.resolver.ModelRemover;
import com.example.blogservice.model.Post;
import com.example.blogservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostModelRemover implements ModelRemover {

    private final CommentService commentService;

    @Override
    public ModelType getModelType() {
        return ModelType.POST;
    }

    @Override
    public void prepareModelRemoving(Object model) {
        log.info("Delete all post's comments with post id : {}", model);
        Post post = (Post) model;
        commentService.deleteAllByPost(post);
    }
}
