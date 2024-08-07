package com.group1.quiz.repository;

import com.group1.quiz.model.AnswerModel;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnswerRepository extends MongoRepository<AnswerModel, String> {
    List<AnswerModel> findByQuestionId(String questionId);
    void deleteAllByQuestionId(String questionId);
}
