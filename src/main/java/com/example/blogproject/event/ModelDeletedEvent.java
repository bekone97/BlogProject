package com.example.blogproject.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModelDeletedEvent {
    private Object model;
    private ModelType modelType;
}
