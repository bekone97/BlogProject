package com.example.blogservice.event.resolver;

import com.example.blogservice.event.ModelType;

public interface ModelRemover {

    ModelType getModelType();

    void prepareModelRemoving(Object model);
}
