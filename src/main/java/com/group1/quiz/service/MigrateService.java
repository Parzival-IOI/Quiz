package com.group1.quiz.service;

import com.group1.quiz.model.UserRole;
import com.group1.quiz.model.UserModel;
import com.group1.quiz.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigrateService {
    private final UserRepository userRepository;
    public void migrate() throws Exception {
        Optional<UserModel> existUser = userRepository.findUserByUsername("admin");
        if(existUser.isEmpty()) {
            UserModel user = new UserModel();
            user.setUsername("admin");
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));
            user.setRole(UserRole.ADMIN);
            userRepository.save(user);
        } else {
            throw new Exception("Username already exists");
        }
    }
}
