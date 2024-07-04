package com.group1.quiz.service;

import com.group1.quiz.dataTransferObject.AnswerDTO.AnswerResponse;
import com.group1.quiz.dataTransferObject.AnswerDTO.CreateAnswerWithQuestion;
import com.group1.quiz.dataTransferObject.AnswerDTO.UpdateANQRequest;
import com.group1.quiz.dataTransferObject.QuestionDTO.CreateQuestionAndAnswerRequest;
import com.group1.quiz.dataTransferObject.QuestionDTO.CreateQuestionRequest;
import com.group1.quiz.dataTransferObject.QuestionDTO.QuestionResponse;
import com.group1.quiz.dataTransferObject.QuestionDTO.UpdateQNARequest;
import com.group1.quiz.dataTransferObject.QuestionDTO.UpdateQuestionRequest;
import com.group1.quiz.enums.UserRoleEnum;
import com.group1.quiz.model.AnswerModel;
import com.group1.quiz.model.QuestionModel;
import com.group1.quiz.model.QuizModel;
import com.group1.quiz.model.UserModel;
import com.group1.quiz.repository.AnswerRepository;
import com.group1.quiz.repository.QuestionRepository;
import com.group1.quiz.repository.QuizRepository;
import com.group1.quiz.repository.UserRepository;
import com.group1.quiz.util.ResponseStatusException;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    public QuestionModel createQuestion(CreateQuestionRequest createQuestionRequest, Principal principal) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        Optional<QuizModel> quizModel = quizRepository.findById(createQuestionRequest.getQuizId());
        if (quizModel.isPresent() && userModel.isPresent()) {
            if(quizModel.get().getUserId().equals(userModel.get().getId()) || userModel.get().getRole().equals(UserRoleEnum.ADMIN)) {
                QuestionModel questionModel = QuestionModel.builder()
                        .question(createQuestionRequest.getQuestion())
                        .type(createQuestionRequest.getType())
                        .quizId(createQuestionRequest.getQuizId())
                        .build();
                questionRepository.insert(questionModel);
                return questionModel;
            }
            else {
                throw new ResponseStatusException("Permission Denied", HttpStatus.FORBIDDEN);
            }
        }
        else {
            throw new ResponseStatusException("Quiz/User not found", HttpStatus.NOT_FOUND);
        }
    }

    public void updateQuestion(String id, UpdateQuestionRequest updateQuestionRequest, Principal principal) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        Optional<QuestionModel> questionModel = questionRepository.findById(id);
        if (questionModel.isPresent() && userModel.isPresent()) {
            Optional<QuizModel> quizModel = quizRepository.findById(questionModel.get().getQuizId());
            if(quizModel.isPresent()) {
                if(quizModel.get().getUserId().equals(userModel.get().getId()) || userModel.get().getRole().equals(UserRoleEnum.ADMIN)) {
                    questionModel.get().setQuestion(updateQuestionRequest.getQuestion());
                    questionModel.get().setType(updateQuestionRequest.getType());

                    questionRepository.save(questionModel.get());
                }
                else {
                    throw new ResponseStatusException("Permission Denied", HttpStatus.FORBIDDEN);
                }
            } else {
                throw new ResponseStatusException("Quiz Not Found", HttpStatus.NOT_FOUND);
            }
        }
        else {
            throw new ResponseStatusException("Question/User not found", HttpStatus.NOT_FOUND);
        }
    }

    public void deleteQuestion(String id, Principal principal) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        Optional<QuestionModel> questionModel = questionRepository.findById(id);
        if (questionModel.isPresent() && userModel.isPresent()) {
            Optional<QuizModel> quizModel = quizRepository.findById(questionModel.get().getQuizId());
            if(quizModel.isPresent()) {
                if (quizModel.get().getUserId().equals(userModel.get().getId()) || userModel.get().getRole().equals(UserRoleEnum.ADMIN)) {
                    questionRepository.deleteById(id);
                }
                else {
                    throw new ResponseStatusException("Permission Denied", HttpStatus.FORBIDDEN);
                }
            } else {
                throw new ResponseStatusException("Quiz Not Found", HttpStatus.NOT_FOUND);
            }
        }
        else {
            throw new ResponseStatusException("Question/User not found", HttpStatus.NOT_FOUND);
        }
    }

    public void createQuestionAndAnswer(CreateQuestionAndAnswerRequest createQuestionAndAnswerRequest, Principal principal) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        Optional<QuizModel> quizModel = quizRepository.findById(createQuestionAndAnswerRequest.getQuizId());
        if (quizModel.isPresent() && userModel.isPresent()) {
            if(quizModel.get().getUserId().equals(userModel.get().getId()) || userModel.get().getRole().equals(UserRoleEnum.ADMIN)) {
                QuestionModel questionModel = QuestionModel.builder()
                        .question(createQuestionAndAnswerRequest.getQuestion())
                        .type(createQuestionAndAnswerRequest.getType())
                        .quizId(createQuestionAndAnswerRequest.getQuizId())
                        .build();
                questionRepository.insert(questionModel);

                for (CreateAnswerWithQuestion createAnswerWithQuestion : createQuestionAndAnswerRequest.getAnswer()) {
                    AnswerModel answerModel = AnswerModel.builder()
                            .answer(createAnswerWithQuestion.getAnswer())
                            .isCorrect(createAnswerWithQuestion.isCorrect())
                            .questionId(questionModel.getId())
                            .build();
                    answerRepository.insert(answerModel);
                }
            }
            else {
                throw new ResponseStatusException("Permission Denied", HttpStatus.FORBIDDEN);
            }
        } else {
            throw new ResponseStatusException("Quiz not found", HttpStatus.NOT_FOUND);
        }
    }

    public QuestionResponse findQNA(String id, Principal principal) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        Optional<QuestionModel> questionModel = questionRepository.findById(id);
        if (questionModel.isPresent() && userModel.isPresent()) {
            Optional<QuizModel> quizModel = quizRepository.findById(questionModel.get().getQuizId());
            if(quizModel.isPresent()) {
                if (quizModel.get().getUserId().equals(userModel.get().getId()) || userModel.get().getRole().equals(UserRoleEnum.ADMIN)) {

                    List<AnswerModel> answerModels = answerRepository.findByQuestionId(questionModel.get().getId());
                    List<AnswerResponse> answerResponses = answerModels.stream().map(this::answerResponseMapping).toList();
                    return QuestionResponse.builder()
                            .id(questionModel.get().getId())
                            .question(questionModel.get().getQuestion())
                            .type(questionModel.get().getType())
                            .answers(answerResponses)
                            .updatedAt(questionModel.get().getUpdatedAt())
                            .createdAt(questionModel.get().getCreatedAt())
                            .build();
                }
                else {
                    throw new ResponseStatusException("Permission Denied", HttpStatus.FORBIDDEN);
                }
            } else {
                throw new ResponseStatusException("Quiz Not Found", HttpStatus.NOT_FOUND);
            }
        }
        else {
            throw new ResponseStatusException("Question/User not found", HttpStatus.NOT_FOUND);
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

    public void updateQNA(String id, UpdateQNARequest updateQNARequest, Principal principal) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        Optional<QuestionModel> questionModel = questionRepository.findById(id);
        if (questionModel.isPresent() && userModel.isPresent()) {
            Optional<QuizModel> quizModel = quizRepository.findById(questionModel.get().getQuizId());
            if(quizModel.isPresent()) {
                if(quizModel.get().getUserId().equals(userModel.get().getId()) || userModel.get().getRole().equals(UserRoleEnum.ADMIN)) {
                    for(UpdateANQRequest updateANQRequest : updateQNARequest.getAnswer()) {
                        Optional<AnswerModel> answerModel = answerRepository.findById(updateANQRequest.getId());

                        if(answerModel.isPresent()) {

                            answerModel.get().setAnswer(updateANQRequest.getAnswer());
                            answerModel.get().setCorrect(updateANQRequest.isCorrect());

                            answerRepository.save(answerModel.get());
                        }
                    }

                    questionModel.get().setQuestion(updateQNARequest.getQuestion());
                    questionModel.get().setType(updateQNARequest.getType());

                    questionRepository.save(questionModel.get());
                }
                else {
                    throw new ResponseStatusException("Permission Denied", HttpStatus.FORBIDDEN);
                }
            } else {
                throw new ResponseStatusException("Quiz Not Found", HttpStatus.NOT_FOUND);
            }
        }
        else {
            throw new ResponseStatusException("Question/User not found", HttpStatus.NOT_FOUND);
        }
    }

}
