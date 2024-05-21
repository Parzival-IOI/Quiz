package com.group1.quiz.repository;

import com.group1.quiz.model.PlayModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayRepository extends MongoRepository<PlayModel, String> {
}
