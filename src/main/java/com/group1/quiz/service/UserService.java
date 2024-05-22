package com.group1.quiz.service;

import com.group1.quiz.dataTransferObject.UserDto;
import com.group1.quiz.model.UserRole;
import com.group1.quiz.model.UserModel;
import com.group1.quiz.repository.UserRepository;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<UserModel> userModel = userRepository.findUserByUsername(username);
        String defaultUsername = "Parzival";
        String defaultPassword = new BCryptPasswordEncoder().encode("123");

        if(userModel.isPresent()) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userModel.get().getUsername())
                    .password(userModel.get().getPassword())
                    .roles(String.valueOf(userModel.get().getRole()))
                    .build();
        }
        else if(Objects.equals(username, defaultUsername)) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(defaultUsername)
                    .password(defaultPassword)
                    .roles(String.valueOf(UserRole.ADMIN))
                    .build();
        }
        return null;
    }
    public void createUser(UserDto userDto){
        UserModel userModel = new UserModel(userDto);
        String encodedPassword = new BCryptPasswordEncoder().encode(userModel.getPassword());
        userModel.setPassword(encodedPassword);
        userRepository.save(userModel);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
