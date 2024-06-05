package com.group1.quiz.repository;

import com.group1.quiz.model.QuestionModel;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends MongoRepository<QuestionModel, String> {
    List<QuestionModel> findByQuizId(String quizId);
}
