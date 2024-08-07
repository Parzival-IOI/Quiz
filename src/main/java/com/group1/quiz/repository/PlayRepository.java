package com.group1.quiz.repository;

import com.group1.quiz.model.PlayModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface PlayRepository extends MongoRepository<PlayModel, String> {
    @Query(value = "{}", count = true)
    long countAllDocuments();

    List<PlayModel> findByQuizId(String quizId);

    List<PlayModel> findByUsername(String username);

    List<PlayModel> findAllByQuizId(String quizId);

    List<PlayModel> findAllByUsername(String username);

    void deleteAllByUsername(String username);

    void deleteAllByQuizId(String quizId);

    Optional<PlayModel> findPlayModelByUsernameAndQuizId (String username, String quizId);

}
