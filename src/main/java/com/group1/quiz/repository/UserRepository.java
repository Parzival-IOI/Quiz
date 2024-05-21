package com.group1.quiz.repository;

import com.group1.quiz.model.UserModel;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserModel, String> {
        Optional<UserModel> findUserByUsername(String name);
}
