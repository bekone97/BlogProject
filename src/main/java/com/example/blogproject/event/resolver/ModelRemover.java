package com.example.blogproject.event.resolver;

import com.example.blogproject.event.ModelType;

public interface ModelRemover {

    ModelType getModelType();

    void prepareModelRemoving(Object model);
}
