package com.example.blogservice.repository;

import com.example.blogservice.model.ModelUpdateStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ModelUpdateStatisticsRepository extends MongoRepository<ModelUpdateStatistics, String> {
}
