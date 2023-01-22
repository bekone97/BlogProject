package com.example.blogproject.service.impl;

import com.example.blogproject.model.sequence.DatabaseSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@RequiredArgsConstructor

public class SequenceGeneratorService {

    private final MongoOperations mongoOperations;
    @Transactional
    public Long generateSequence(String seqName){
        DatabaseSequence counter = mongoOperations.findAndModify(new Query(where("id").is(seqName)),
                new Update().inc("seq", 1L), options().returnNew(true).upsert(true), DatabaseSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1L;
    }
}
