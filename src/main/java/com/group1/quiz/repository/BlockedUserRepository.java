package com.group1.quiz.repository;

import com.group1.quiz.model.BlockedUserModel;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlockedUserRepository extends MongoRepository<BlockedUserModel, String> {
    Optional<BlockedUserModel> findByUsername(String username);
}
