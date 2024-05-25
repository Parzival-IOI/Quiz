package com.group1.quiz.repository;

import com.group1.quiz.model.PlayModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface PlayRepository extends MongoRepository<PlayModel, String> {
    @Query(value = "{}", count = true)
    long countAllDocuments();
}
