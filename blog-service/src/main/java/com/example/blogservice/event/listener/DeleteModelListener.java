package com.example.blogservice.event.listener;

import com.example.blogservice.event.ModelDeletedEvent;
import com.example.blogservice.event.resolver.DeleteModelResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteModelListener {
    private final DeleteModelResolver deleteModelResolver;

    @EventListener
    public void handleDeleteAction(ModelDeletedEvent modelDeletedEvent) {
        log.info("Received modelDeletedEvent = {}", modelDeletedEvent);
        deleteModelResolver.prepareModelRemoving(modelDeletedEvent);
    }
}
