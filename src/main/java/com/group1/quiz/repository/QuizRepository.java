package com.group1.quiz.repository;

import com.group1.quiz.model.QuizModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizRepository extends MongoRepository<QuizModel, String> {
}