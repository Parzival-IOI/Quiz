package com.group1.quiz.service;

import com.group1.quiz.dataTransferObject.TableResponse;
import com.group1.quiz.dataTransferObject.UserDTO.UserRegisterRequest;
import com.group1.quiz.dataTransferObject.UserDTO.UserRequest;
import com.group1.quiz.dataTransferObject.UserDTO.UserResponse;
import com.group1.quiz.enums.OrderEnum;
import com.group1.quiz.enums.UserOrderByEnum;
import com.group1.quiz.enums.UserRoleEnum;
import com.group1.quiz.model.UserModel;
import com.group1.quiz.repository.UserRepository;
import com.group1.quiz.util.ResponseStatusException;
import com.group1.quiz.util.TableQueryBuilder;
import java.security.Principal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<UserModel> userModel = userRepository.findUserByUsername(username);

        return userModel.map(model -> org.springframework.security.core.userdetails.User.builder()
                .username(model.getUsername())
                .password(model.getPassword())
                .roles(model.getRole().getValue())
                .build()).orElse(null);
    }
    public void createUser(UserRequest userDto) throws Exception {
        if(userRepository.findUserByUsername(userDto.getUsername()).isPresent()) {
            throw new ResponseStatusException("Username already exists", HttpStatus.BAD_REQUEST);
        }
        UserModel userModel = new UserModel(userDto);
        String encodedPassword = new BCryptPasswordEncoder().encode(userModel.getPassword());
        userModel.setPassword(encodedPassword);
        userModel.setCreatedAt(Date.from(Instant.now()));
        userModel.setUpdatedAt(Date.from(Instant.now()));
        userRepository.insert(userModel);
    }


    public void updateUser(String id, UserRequest userDto) throws ResponseStatusException {
        Optional<UserModel> userModel = userRepository.findById(id);
        if(userModel.isPresent()) {
            UserModel user = userMapping(userModel.get(), userDto);
            userRepository.save(user);
        }
        else {
            throw new ResponseStatusException("User not found", HttpStatus.NOT_FOUND);
        }
    }

    private UserModel userMapping(UserModel userModel, UserRequest userDto) {
        return UserModel.builder()
                .id(userModel.getId())
                .username(userDto.getUsername())
                .password(new BCryptPasswordEncoder().encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .role(userDto.getRole())
                .createdAt(userModel.getCreatedAt())
                .updatedAt(Date.from(Instant.now()))
                .build() ;
    }

    public void deleteUser(String id) throws ResponseStatusException {
        if(userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
        else {
            throw new ResponseStatusException("User not found", HttpStatus.NOT_FOUND);
        }
    }

    public TableResponse<UserResponse> getUsers(UserOrderByEnum orderBy, OrderEnum order, int page, int size, String search) throws Exception {
        long count;
        TableQueryBuilder tableQueryBuilder = new TableQueryBuilder(search, orderBy.getValue(), order, page, size);

        List<UserModel> userModels = mongoTemplate.find(tableQueryBuilder.getQuery(), UserModel.class);

        if (!StringUtils.isEmpty(search)) {
            count = userModels.size();
        } else {
            count = userRepository.countAllDocuments();
        }
        return TableResponse.<UserResponse>builder()
                .data(userModels.stream().map(this::userResponseMapping).toList())
                .columns(count)
                .build();
    }

    private UserResponse userResponseMapping(UserModel userModel) {
        return UserResponse.builder()
                .name(userModel.getUsername())
                .email(userModel.getEmail())
                .role(userModel.getRole())
                .createdAt(userModel.getCreatedAt())
                .updatedAt(userModel.getUpdatedAt())
                .build();
    }

    public UserResponse findOne(String id) throws Exception {
        Optional<UserModel> userModel = userRepository.findById(id);
        if(userModel.isPresent()) {
            return UserResponse.builder()
                    .name(userModel.get().getUsername())
                    .email(userModel.get().getEmail())
                    .role(userModel.get().getRole())
                    .createdAt(userModel.get().getCreatedAt())
                    .updatedAt(userModel.get().getUpdatedAt())
                    .build();
        }
        else {
            throw new ResponseStatusException("User not found", HttpStatus.NOT_FOUND);
        }
    }

    public void registerUser(UserRegisterRequest userRegisterRequest) throws Exception {
        if(userRepository.existsByUsername(userRegisterRequest.getUsername()) || userRepository.existsByEmail(userRegisterRequest.getEmail())) {
            throw new ResponseStatusException("Username/Email already exists", HttpStatus.BAD_REQUEST);
        }

        UserRoleEnum role;
        if(Objects.equals(userRegisterRequest.getRole().getValue(), UserRoleEnum.TEACHER.getValue())) {
            role = UserRoleEnum.TEACHER;
        } else if(Objects.equals(userRegisterRequest.getRole().getValue(), UserRoleEnum.STUDENT.getValue())) {
            role = UserRoleEnum.STUDENT;
        } else {
            role = UserRoleEnum.STUDENT;
        }

        UserModel user = UserModel.builder()
                .username(userRegisterRequest.getUsername())
                .password(new BCryptPasswordEncoder().encode(userRegisterRequest.getPassword()))
                .email(userRegisterRequest.getEmail())
                .role(role)
                .createdAt(Date.from(Instant.now()))
                .updatedAt(Date.from(Instant.now()))
                .build();

        userRepository.save(user);
    }

    public String getRole(Principal principal) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        log.info(principal.getName());
        if(userModel.isPresent()) {
            return userModel.get().getRole().getValue();
        } else {
            throw new ResponseStatusException("User not found", HttpStatus.NOT_FOUND);
        }
    }
}
