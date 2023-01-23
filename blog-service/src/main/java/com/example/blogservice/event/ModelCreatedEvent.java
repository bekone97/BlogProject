package com.example.blogservice.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModelCreatedEvent {
    private long modelId;
    private String modelName;
}

