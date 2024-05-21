package com.group1.quiz.repository;

import com.group1.quiz.model.QuestionModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuestionRepository extends MongoRepository<QuestionModel, String> {
}
