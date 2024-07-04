package com.group1.quiz.repository;

import com.group1.quiz.model.QuizModel;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface QuizRepository extends MongoRepository<QuizModel, String> {
    List<QuizModel> findByUserId(String userId);
    @Query(value = "{}", count = true)
    Long countAllDocuments();

    List<QuizModel> findAllByUserId(String userId);

    void deleteByUserId(String userId);
}
