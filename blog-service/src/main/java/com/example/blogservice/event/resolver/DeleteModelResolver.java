package com.example.blogservice.event.resolver;

import com.example.blogservice.event.ModelDeletedEvent;
import com.example.blogservice.event.ModelType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DeleteModelResolver {
    private final Map<ModelType, ModelRemover> modelTypeRemoverContexts;

    @Autowired
    public DeleteModelResolver(List<ModelRemover> modelRemovers) {
        this.modelTypeRemoverContexts= modelRemovers.stream()
                .collect(Collectors.toMap(ModelRemover::getModelType, Function.identity()));
    }

    public void prepareModelRemoving(ModelDeletedEvent modelDeletedEvent){
        modelTypeRemoverContexts.get(modelDeletedEvent.getModelType())
                .prepareModelRemoving(modelDeletedEvent.getModel());
    }
}
