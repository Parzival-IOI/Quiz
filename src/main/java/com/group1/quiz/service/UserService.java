package com.group1.quiz.service;

import com.group1.quiz.dataTransferObject.AuthResponse;
import com.group1.quiz.dataTransferObject.OtpRequest;
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
import com.group1.quiz.model.RegisterModel;
import com.group1.quiz.model.UserModel;
import com.group1.quiz.repository.AnswerRepository;
import com.group1.quiz.repository.LoginRepository;
import com.group1.quiz.repository.PlayRepository;
import com.group1.quiz.repository.QuestionRepository;
import com.group1.quiz.repository.QuizRepository;
import com.group1.quiz.repository.RegisterRepository;
import com.group1.quiz.repository.UserRepository;
import com.group1.quiz.util.ResponseStatusException;
import com.group1.quiz.util.TableQueryBuilder;
import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
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
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
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
    private final MailService mailService;
    private final JwtEncoder jwtEncoder;
    private final RegisterRepository registerRepository;

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

    private PlayModel updatePlayUsername (PlayModel playModel, String username) {
        playModel.setUsername(username);
        return playModel;
    }

    private UserModel userMapping(UserModel userModel, UserRequest userDto) {
        userModel.setUsername(userDto.getUsername());
        userModel.setEmail(userDto.getEmail());
        userModel.setRole(userDto.getRole());
        if(userDto.getPassword().isEmpty()) {
            return userModel;
        }
        userModel.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));
        return userModel;
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
        if (userRepository.existsByUsername(userRegisterRequest.getUsername()) || userRepository.existsByEmail(userRegisterRequest.getEmail())) {
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
                .build();

        userRepository.insert(user);
    }

    public String registerUserOtp(UserRegisterRequest userRegisterRequest) throws Exception {
        if (userRepository.existsByUsername(userRegisterRequest.getUsername()) || userRepository.existsByEmail(userRegisterRequest.getEmail())) {
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
                .build();



        Optional<RegisterModel> registerModel = registerRepository.findRegisterByUserEmail(userRegisterRequest.getEmail());

        if (registerModel.isPresent()) {
            registerModel.get().setUser(user);
            registerRepository.save(registerModel.get());
            return user.getEmail();
        }

        String otp = mailService.sendOTP(userRegisterRequest.getEmail().trim(), "Registration With Quiz Application");

        registerRepository.insert(
                RegisterModel.builder()
                        .user(user)
                        .otp(otp)
                        .attempt((byte) 0)
                        .resend((byte) 0)
                        .build()
        );

        return user.getEmail();
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

    public AuthResponse authenticateEmail(OtpRequest otp) throws Exception {
        Optional<RegisterModel> registerModel = registerRepository.findRegisterByUserEmail(otp.getEmail());
        if(registerModel.isPresent()) {
            byte attempt = registerModel.get().getAttempt();
            byte resend = registerModel.get().getResend();
            if(attempt >= 5  || resend >= 5) {
                throw new ResponseStatusException("Blocked", HttpStatus.FORBIDDEN);
            }
            Date updatedDate = registerModel.get().getUpdatedAt();
            Date now = Date.from(Instant.now());
            long time = now.getTime() - updatedDate.getTime();
            long diff = TimeUnit.MINUTES.convert(time, TimeUnit.MILLISECONDS);
            if(diff > 5) {
                throw new ResponseStatusException("OTP is Out of Time", HttpStatus.BAD_REQUEST);
            }

            if(registerModel.get().getOtp().equals(otp.getOtp())) {
                userRepository.insert(
                        UserModel.builder()
                                .username(registerModel.get().getUser().getUsername())
                                .password(registerModel.get().getUser().getPassword())
                                .email(registerModel.get().getUser().getEmail())
                                .role(registerModel.get().getUser().getRole())
                                .build()
                );
                Instant rightNow = Instant.now();
                String role = registerModel.get().getUser().getRole().getValue();
                //access token
                JwtClaimsSet accessToken = JwtClaimsSet.builder()
                        .issuer("self")
                        .issuedAt(rightNow)
                        .expiresAt(rightNow.plusSeconds(15*60))
                        .subject(registerModel.get().getUser().getUsername())
                        .claim("role", "ROLE_" + role)
                        .build();

                //refresh token
                JwtClaimsSet refreshToken = JwtClaimsSet.builder()
                        .issuer("self")
                        .issuedAt(rightNow)
                        .expiresAt(rightNow.plus(1, ChronoUnit.HOURS))
                        .subject(registerModel.get().getUser().getUsername())
                        .claim("role", "ROLE_REFRESH_TOKEN")
                        .claim("token", "refresh")
                        .build();

                String generatedAccessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(accessToken)).getTokenValue();
                String generatedRefreshToken = this.jwtEncoder.encode(JwtEncoderParameters.from(refreshToken)).getTokenValue();
                registerRepository.deleteById(registerModel.get().getId());

                return AuthResponse.builder()
                        .accessToken(generatedAccessToken)
                        .refreshToken(generatedRefreshToken)
                        .build();
            }
            else {
                registerModel.get().setAttempt((byte) (attempt + 1));
                registerRepository.save(registerModel.get());
                throw new ResponseStatusException("Invalid OTP", HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new ResponseStatusException("Invalid Email", HttpStatus.NOT_FOUND);
        }
    }

    public void resendOTP(OtpRequest otpRequest) throws Exception {
        Optional<RegisterModel> registerModel = registerRepository.findRegisterByUserEmail(otpRequest.getEmail());
        if(registerModel.isPresent()) {
            byte attempt = registerModel.get().getAttempt();
            byte resend = registerModel.get().getResend();
            if(attempt >= 5 || resend >= 5) {
                throw new ResponseStatusException("Email Blocked", HttpStatus.FORBIDDEN);
            }

            String otp = mailService.sendOTP(otpRequest.getEmail().trim(), "Registration With Quiz Application");

            registerModel.get().setOtp(otp);
            registerModel.get().setResend((byte) (resend+1));

            registerRepository.save(registerModel.get());

        } else {
            throw new ResponseStatusException("Invalid Email", HttpStatus.NOT_FOUND);
        }

    }
}
