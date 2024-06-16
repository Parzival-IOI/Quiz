package com.group1.quiz.repository;

import com.group1.quiz.model.UserModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserModel, String> {
    Optional<UserModel> findUserByUsername(String name);

    List<UserModel> findAllByUsername(String name);
    List<UserModel> findAllByEmail(String email);

    @Query(value = "{}", count = true)
    long countAllDocuments();

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
