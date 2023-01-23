package com.example.blogservice.service.impl;

import com.example.blogservice.model.ModelUpdateStatistics;
import com.example.blogservice.repository.ModelUpdateStatisticsRepository;
import com.example.blogservice.service.ModelUpdateStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelUpdateStatisticsServiceImpl implements ModelUpdateStatisticsService {

    private final ModelUpdateStatisticsRepository modelUpdateStatisticsRepository;
    private final MongoOperations mongoOperations;


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void update(ModelUpdateStatistics updateModelInf) {
         mongoOperations.findAndModify(new Query(Criteria.where("id").is(updateModelInf.getId())),
                new Update().inc("updateCount", 1), options().returnNew(false), ModelUpdateStatistics.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(ModelUpdateStatistics createModelInf) {
        log.info("Make save modelUpdateStistics = {}",createModelInf);
        modelUpdateStatisticsRepository.save(createModelInf);
    }
}
