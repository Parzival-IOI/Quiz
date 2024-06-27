package com.group1.quiz.service;

import com.group1.quiz.model.UserModel;
import com.group1.quiz.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserImplementService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserModel> userModel = userRepository.findUserByUsername(username);

        if(userModel.isPresent()) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userModel.get().getUsername())
                    .password(userModel.get().getPassword())
                    .roles(userModel.get().getRole().getValue())
                    .build();
        }
        throw new RuntimeException("User Not Found");
    }
}
