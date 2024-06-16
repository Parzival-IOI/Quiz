package com.group1.quiz.service;


import com.group1.quiz.dataTransferObject.AnswerDTO.AnswerRequest;
import com.group1.quiz.dataTransferObject.AnswerDTO.AnswerResponse;
import com.group1.quiz.dataTransferObject.QuestionDTO.QuestionResponse;
import com.group1.quiz.dataTransferObject.QuizDTO.CreateQuizRequest;
import com.group1.quiz.dataTransferObject.QuestionDTO.QuestionRequest;
import com.group1.quiz.dataTransferObject.QuizDTO.UpdateQuizRequest;
import com.group1.quiz.enums.OrderEnum;
import com.group1.quiz.enums.QuizOrderByEnum;
import com.group1.quiz.dataTransferObject.QuizDTO.QuizResponse;
import com.group1.quiz.dataTransferObject.TableResponse;
import com.group1.quiz.dataTransferObject.QuizDTO.QuizzesResponse;
import com.group1.quiz.enums.UserRoleEnum;
import com.group1.quiz.model.AnswerModel;
import com.group1.quiz.model.QuestionModel;
import com.group1.quiz.model.QuizModel;
import com.group1.quiz.enums.QuizVisibilityEnum;
import com.group1.quiz.model.UserModel;
import com.group1.quiz.repository.AnswerRepository;
import com.group1.quiz.repository.QuestionRepository;
import com.group1.quiz.repository.QuizRepository;
import com.group1.quiz.repository.UserRepository;
import com.group1.quiz.util.ResponseStatusException;
import com.group1.quiz.util.TableQueryBuilder;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final MongoTemplate mongoTemplate;

    public List<QuizzesResponse> getSelfQuiz(Principal principal) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        if(userModel.isPresent()) {
            List<QuizModel> quizModels = quizRepository.findByUserId(userModel.get().getId());
            List<QuizzesResponse> quizzesResponses = new ArrayList<>();
            for(QuizModel quizModel : quizModels) {
                quizzesResponses.add(quizResponseMapping(quizModel));
            }
            return quizzesResponses;
        } else {
            throw new ResponseStatusException("No Quiz", HttpStatus.NO_CONTENT);
        }
    }

    public TableResponse<QuizzesResponse> getQuizzes(QuizOrderByEnum orderBy, OrderEnum order, int page, int size, String search) throws Exception {
        long count;
        TableQueryBuilder tableQueryBuilder = new TableQueryBuilder(search, "name", orderBy.getValue(), order, page, size);

        List<QuizModel> quizModels = mongoTemplate.find(tableQueryBuilder.getQuery(), QuizModel.class);

        if (!StringUtils.isEmpty(search)) {
            count = quizModels.size();
        } else {
            count = quizRepository.countAllDocuments();
        }
        return TableResponse.<QuizzesResponse>builder()
                .data(quizModels.stream().map(this::quizResponseMapping).toList())
                .columns(count)
                .build();
    }

    private QuizzesResponse quizResponseMapping(QuizModel quizModel) {
        return QuizzesResponse.builder()
                .id(quizModel.getId())
                .name(quizModel.getName())
                .description(quizModel.getDescription())
                .visibility(String.valueOf(quizModel.getVisibility()))
                .createdAt(quizModel.getCreatedAt())
                .updatedAt(quizModel.getUpdatedAt())
                .build();
    }

    public QuizResponse getQuizById(String id, Principal principal) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        if(userModel.isPresent()) {
            Optional<QuizModel> quizModel = quizRepository.findById(id);
            if(quizModel.isPresent()) {
                if(quizModel.get().getUserId().equals(userModel.get().getId()) || userModel.get().getRole().equals(UserRoleEnum.ADMIN)) {
                    List<QuestionModel> questionModels = questionRepository.findByQuizId(quizModel.get().getId());
                    List<QuestionResponse> questionResponses = new ArrayList<>();
                    for(QuestionModel questionModel : questionModels) {
                        List<AnswerModel> answerModels = answerRepository.findByQuestionId(questionModel.getId());
                        List<AnswerResponse> answerResponses = answerModels.stream().map(this::answerResponseMapping).toList();
                        questionResponses.add(questionResponseMapping(questionModel, answerResponses));
                    }
                    return quizResponseMapping(quizModel.get(), questionResponses);
                }
                else {
                    throw new ResponseStatusException("Permission Denied", HttpStatus.FORBIDDEN);
                }
            }
            else {
                throw new ResponseStatusException("Quiz Not Found", HttpStatus.NOT_FOUND);
            }
        }
        else {
            throw new ResponseStatusException("Permission Denied", HttpStatus.FORBIDDEN);
        }
    }

    private AnswerResponse answerResponseMapping(AnswerModel answerModel) {
        return AnswerResponse.builder()
                .id(answerModel.getId())
                .answer(answerModel.getAnswer())
                .isCorrect(answerModel.isCorrect())
                .createdAt(answerModel.getCreatedAt())
                .updatedAt(answerModel.getUpdatedAt())
                .build();
    }

    private QuestionResponse questionResponseMapping(QuestionModel questionModel, List<AnswerResponse> answerResponses) {
        return QuestionResponse.builder()
                .id(questionModel.getId())
                .question(questionModel.getQuestion())
                .type(questionModel.getType())
                .answers(answerResponses)
                .createdAt(questionModel.getCreatedAt())
                .updatedAt(questionModel.getUpdatedAt())
                .build();
    }

    private QuizResponse quizResponseMapping(QuizModel quizModel, List<QuestionResponse> questionResponses) {
        return QuizResponse.builder()
                .id(quizModel.getId())
                .name(quizModel.getName())
                .description(quizModel.getDescription())
                .visibility(quizModel.getVisibility())
                .questions(questionResponses)
                .createdAt(quizModel.getCreatedAt())
                .updatedAt(quizModel.getUpdatedAt())
                .build();
    }

    public void createQuiz(CreateQuizRequest createQuizRequest, Principal principal) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        if (userModel.isPresent()) {
            QuizModel quizModel = new QuizModel();
            quizModel.setName(createQuizRequest.getName());
            quizModel.setDescription(createQuizRequest.getDescription());
            quizModel.setVisibility(QuizVisibilityEnum.valueOf(createQuizRequest.getVisibility()));
            quizModel.setUserId(userModel.get().getId());
            quizModel.setCreatedAt(Date.from(Instant.now()));
            quizModel.setUpdatedAt(Date.from(Instant.now()));
            quizRepository.save(quizModel);

            List<QuestionRequest> questionModelList = createQuizRequest.getQuestions();
            for (QuestionRequest questionRequest : questionModelList) {
                QuestionModel questionModel = QuestionModel.builder()
                        .quizId(quizModel.getId())
                        .question(questionRequest.getQuestion())
                        .type(questionRequest.getType())
                        .createdAt(Date.from(Instant.now()))
                        .updatedAt(Date.from(Instant.now()))
                        .build();

                questionRepository.save(questionModel);

                List<AnswerRequest> answerRequests = questionRequest.getAnswers();
                for (AnswerRequest answerRequest : answerRequests) {
                    AnswerModel answerModel = new AnswerModel();
                    answerModel.setAnswer(answerRequest.getAnswer());
                    answerModel.setQuestionId(questionModel.getId());
                    answerModel.setCorrect(answerRequest.isCorrect());
                    answerModel.setCreatedAt(Date.from(Instant.now()));
                    answerModel.setUpdatedAt(Date.from(Instant.now()));

                    answerRepository.save(answerModel);
                }
            }
        }
        else {
            throw new ResponseStatusException("User not found", HttpStatus.NOT_FOUND);
        }

    }

    public void updateQuiz(String id, UpdateQuizRequest updateQuizRequest) throws Exception {
        Optional<QuizModel> quizModel = quizRepository.findById(id);
        if(quizModel.isPresent()) {
            QuizModel quiz = QuizModel.builder()
                    .id(quizModel.get().getId())
                    .name(updateQuizRequest.getName())
                    .description(updateQuizRequest.getDescription())
                    .visibility(updateQuizRequest.getVisibility())
                    .userId(quizModel.get().getUserId())
                    .createdAt(quizModel.get().getCreatedAt())
                    .updatedAt(Date.from(Instant.now()))
                    .build();
            quizRepository.save(quiz);
        }
        else {
            throw new ResponseStatusException("Quiz Not Found", HttpStatus.NOT_FOUND);
        }
    }

    public void deleteQuiz(String id) throws Exception {
        if(quizRepository.existsById(id)) {
            quizRepository.deleteById(id);
        }
        else {
            throw new ResponseStatusException("Quiz Not Found", HttpStatus.NOT_FOUND);
        }
    }
}
