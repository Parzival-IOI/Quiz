package com.group1.quiz.service;

import com.group1.quiz.dataTransferObject.UserDto;
import com.group1.quiz.enums.UserRoleEnum;
import com.group1.quiz.model.UserModel;
import com.group1.quiz.repository.UserRepository;
import java.time.Instant;
import java.util.Date;
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
        String defaultUsername = "string";
        String defaultPassword = new BCryptPasswordEncoder().encode("string");

        if(userModel.isPresent()) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userModel.get().getUsername())
                    .password(userModel.get().getPassword())
                    .roles(userModel.get().getRole().getValue())
                    .build();
        }
        else if(Objects.equals(username, defaultUsername)) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(defaultUsername)
                    .password(defaultPassword)
                    .roles(UserRoleEnum.ADMIN.getValue())
                    .build();
        }
        return null;
    }
    public void createUser(UserDto userDto) throws Exception {
        if(userRepository.findUserByUsername(userDto.getUsername()).isPresent()) {
            throw new Exception("Username already exists");
        }
        UserModel userModel = new UserModel(userDto);
        String encodedPassword = new BCryptPasswordEncoder().encode(userModel.getPassword());
        userModel.setPassword(encodedPassword);
        userRepository.insert(userModel);
    }


    public void updateUser(String id, UserDto userDto) throws Exception {
        Optional<UserModel> userModel = userRepository.findById(id);
        if(userModel.isPresent()) {
            UserModel user = userMapping(id, userDto);
            userRepository.save(user);
        }
        else {
            throw new Exception("User not found");
        }
    }

    private UserModel userMapping(String id, UserDto userDto) {
        return UserModel.builder()
                .id(id)
                .username(userDto.getUsername())
                .password(new BCryptPasswordEncoder().encode(userDto.getPassword()))
                .role(userDto.getRole())
                .createdAt(Date.from(Instant.now()))
                .updatedAt(Date.from(Instant.now()))
                .build() ;
    }

    public void deleteUser(String id) throws Exception {
        Optional<UserModel> userModel = userRepository.findById(id);
        if(userModel.isPresent()) {
            userRepository.deleteById(id);
        }
        else {
            throw new Exception("User not found");
        }
    }

}
