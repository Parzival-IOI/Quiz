package com.group1.quiz.service;

import com.group1.quiz.dataTransferObject.TableResponse;
import com.group1.quiz.dataTransferObject.UserDTO.UserRegisterRequest;
import com.group1.quiz.dataTransferObject.UserDTO.UserRequest;
import com.group1.quiz.dataTransferObject.UserDTO.UserResponse;
import com.group1.quiz.enums.OrderEnum;
import com.group1.quiz.enums.UserOrderByEnum;
import com.group1.quiz.enums.UserRoleEnum;
import com.group1.quiz.model.LoginModel;
import com.group1.quiz.model.PlayModel;
import com.group1.quiz.model.QuestionModel;
import com.group1.quiz.model.QuizModel;
import com.group1.quiz.model.UserModel;
import com.group1.quiz.repository.AnswerRepository;
import com.group1.quiz.repository.LoginRepository;
import com.group1.quiz.repository.PlayRepository;
import com.group1.quiz.repository.QuestionRepository;
import com.group1.quiz.repository.QuizRepository;
import com.group1.quiz.repository.UserRepository;
import com.group1.quiz.util.ResponseStatusException;
import com.group1.quiz.util.TableQueryBuilder;
import java.security.Principal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.swing.text.html.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final LoginRepository loginRepository;
    private final PlayRepository playRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

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
    public void createUser(UserRequest userDto) throws Exception {
        boolean isUserExist = userRepository.existsByUsername(userDto.getUsername());
        boolean isEmailExist = userRepository.existsByEmail(userDto.getEmail());
        if(isUserExist || isEmailExist) {
            throw new ResponseStatusException("User or Email is Already in used", HttpStatus.BAD_REQUEST);
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

        List<UserModel> AllUsername = userRepository.findAllByUsername(userDto.getUsername());
        List<UserModel> AllEmail = userRepository.findAllByEmail(userDto.getEmail());

        for(UserModel user : AllUsername) {
            if(user.getId().equals(id)) {
                continue;
            }
            if(user.getUsername().equals(userDto.getUsername())) {
                throw new ResponseStatusException("Username already exists", HttpStatus.BAD_REQUEST);
            }
        }

        for(UserModel user : AllEmail) {
            if(user.getId().equals(id)) {
                continue;
            }
            if(user.getEmail().equals(userDto.getEmail())) {
                throw new ResponseStatusException("Email already exists", HttpStatus.BAD_REQUEST);
            }
        }

        if(userModel.isPresent()) {
            UserModel user = userMapping(userModel.get(), userDto);
            userRepository.save(user);

            List<PlayModel> playModels = playRepository.findByUsername(user.getUsername());
            List<PlayModel> playModelsUpdate = playModels.stream().map(e->this.updatePlayUsername(e, user.getUsername())).toList();
            playRepository.saveAll(playModelsUpdate);
        }
        else {
            throw new ResponseStatusException("User not found", HttpStatus.NOT_FOUND);
        }
    }

    private PlayModel updatePlayUsername (PlayModel playModel, String usernanme) {
        playModel.setUsername(usernanme);
        return playModel;
    }

    private UserModel userMapping(UserModel userModel, UserRequest userDto) {

        if(userDto.getPassword().isEmpty()) {
            return UserModel.builder()
                    .id(userModel.getId())
                    .username(userDto.getUsername())
                    .password(userModel.getPassword())
                    .email(userDto.getEmail())
                    .role(userDto.getRole())
                    .createdAt(userModel.getCreatedAt())
                    .updatedAt(Date.from(Instant.now()))
                    .build() ;
        }
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
        Optional<UserModel> userModel = userRepository.findById(id);
        if(userModel.isPresent()) {
            // delete all user played other quiz by username
            playRepository.deleteAllByUsername(userModel.get().getUsername());

            // find all quizzes to delete questions
            List<QuizModel> quizModels = quizRepository.findByUserId(userModel.get().getId());
            for(QuizModel quizModel : quizModels) {
                // delete other  record played this quiz (created by this deleted user)
                playRepository.deleteAllByQuizId(quizModel.getId());

                // find all questions to delete answers
                List<QuestionModel> questionModels = questionRepository.findByQuizId(quizModel.getId());
                for(QuestionModel questionModel : questionModels) {
                    //delete all answer related to this each question
                    answerRepository.deleteAllByQuestionId(questionModel.getId());
                }
                // delete all questions related to each quiz
                questionRepository.deleteByQuizId(quizModel.getId());
            }
            // delete all quiz
            quizRepository.deleteByUserId(userModel.get().getId());
            //delete user last
            userRepository.deleteById(id);
        }
        else {
            throw new ResponseStatusException("User not found", HttpStatus.NOT_FOUND);
        }
    }

    public TableResponse<UserResponse> getUsers(UserOrderByEnum orderBy, OrderEnum order, int page, int size, String search) throws Exception {
        long count;
        TableQueryBuilder tableQueryBuilder = new TableQueryBuilder(search, "username", orderBy.getValue(), order, page, size);

        List<UserModel> userModels = mongoTemplate.find(tableQueryBuilder.getQuery(), UserModel.class);

        if (!StringUtils.isEmpty(search)) {
            Query query = Query.query(Criteria.where("username").regex(".*"+search+".*", "i"));
            count = mongoTemplate.find(query, UserModel.class).size();
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
                .id(userModel.getId())
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
                    .id(userModel.get().getId())
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

    public void logout(Principal principal, Jwt jwt) throws Exception {
        Optional<LoginModel> loginModel = loginRepository.findByUserName(principal.getName());
        if(loginModel.isPresent()) {
            if(loginModel.get().getRefreshToken().equals(jwt.getTokenValue())){
                loginRepository.delete(loginModel.get());
            }
            else {
                throw new ResponseStatusException("Token incorrect", HttpStatus.BAD_REQUEST);
            }
        }
        else {
            throw new ResponseStatusException("User Not Found", HttpStatus.BAD_REQUEST);
        }
    }
}
