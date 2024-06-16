package com.group1.quiz.repository;

import com.group1.quiz.model.LoginModel;
import com.group1.quiz.model.UserModel;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoginRepository extends MongoRepository<LoginModel, String> {
    Optional<LoginModel> findByUserName(String userName);
}
