package com.example.blogservice.event.listener;

import com.example.blogservice.event.ModelCreatedEvent;
import com.example.blogservice.event.ModelUpdatedEvent;
import com.example.blogservice.model.ModelUpdateStatistics;
import com.example.blogservice.service.ModelUpdateStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableAsync(proxyTargetClass = true)
public class StatisticModelListener implements AsyncConfigurer {

    public static final String MODEL_STATISTIC_ID_PATTERN = "%s-%s";
    private final ModelUpdateStatisticsService modelUpdateStatisticsService;

    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUpdateTransaction(ModelUpdatedEvent modelUpdatedEvent) {
        log.info("Received modelUpdatedEvent = {}", modelUpdatedEvent);
        modelUpdateStatisticsService.update(ModelUpdateStatistics.builder()
                .id(String.format(MODEL_STATISTIC_ID_PATTERN, modelUpdatedEvent.getModelId(), modelUpdatedEvent.getModelName()))
                .modelId(modelUpdatedEvent.getModelId())
                .modelName(modelUpdatedEvent.getModelName())
                .build());
    }

    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCreateTransaction(ModelCreatedEvent modelCreatedEvent) {
        log.info("Received modelCreatedEvent = {}", modelCreatedEvent);
        modelUpdateStatisticsService.save(ModelUpdateStatistics.builder()
                .id(String.format(MODEL_STATISTIC_ID_PATTERN, modelCreatedEvent.getModelId(), modelCreatedEvent.getModelName()))
                .modelId(modelCreatedEvent.getModelId())
                .modelName(modelCreatedEvent.getModelName())
                .build());

    }
}
