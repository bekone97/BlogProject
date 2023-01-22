package com.example.blogproject.repository;

import com.example.blogproject.model.ModelUpdateStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ModelUpdateStatisticsRepository extends MongoRepository<ModelUpdateStatistics,String> {
}
