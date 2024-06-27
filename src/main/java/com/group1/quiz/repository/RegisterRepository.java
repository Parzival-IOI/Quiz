package com.group1.quiz.repository;

import com.group1.quiz.model.RegisterModel;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RegisterRepository extends MongoRepository<RegisterModel, String> {
    Optional<RegisterModel> findRegisterByUserEmail (String email);

    boolean existsByUserEmail (String email);

}
