package com.example.blogservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "model_update_statistics")
public class ModelUpdateStatistics {

    @Id
    private String id;

    private String modelName;

    private Long modelId;

    private long updateCount;
}
