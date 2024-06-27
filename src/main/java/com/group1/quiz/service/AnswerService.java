package com.group1.quiz.service;

import com.group1.quiz.dataTransferObject.AnswerDTO.CreateAnswerRequest;
import com.group1.quiz.dataTransferObject.AnswerDTO.UpdateAnswerRequest;
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
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;

    public AnswerModel createAnswer(CreateAnswerRequest createAnswerRequest, Principal principal) throws Exception {
        this.validateAnswer(principal.getName(), createAnswerRequest.getQuestionId());
        AnswerModel answerModel = AnswerModel.builder()
                .answer(createAnswerRequest.getAnswer())
                .isCorrect(createAnswerRequest.isCorrect())
                .questionId(createAnswerRequest.getQuestionId())
                .build();
        answerRepository.insert(answerModel);
        return answerModel;
    }

    public void updateAnswer(String id, UpdateAnswerRequest updateAnswerRequest, Principal principal) throws Exception {
        Optional<AnswerModel> answerModel = answerRepository.findById(id);
        if (answerModel.isPresent()) {
            this.validateAnswer(principal.getName(), answerModel.get().getQuestionId());

            answerModel.get().setAnswer(updateAnswerRequest.getAnswer());
            answerModel.get().setCorrect(updateAnswerRequest.isCorrect());

            answerRepository.save(answerModel.get());
        }
        else {
            throw new ResponseStatusException("Answer not found", HttpStatus.NOT_FOUND);
        }
    }

    public void deleteAnswer(String id, Principal principal) throws Exception {
        Optional<AnswerModel> answerModel = answerRepository.findById(id);
        if (answerModel.isPresent()) {
            this.validateAnswer(principal.getName(), answerModel.get().getQuestionId());
            answerRepository.deleteById(id);
        }
        else {
            throw new ResponseStatusException("Answer not found", HttpStatus.NOT_FOUND);
        }
    }

    private void validateAnswer(String username, String questionId) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(username);
        Optional<QuestionModel> questionModel = questionRepository.findById(questionId);
        if(userModel.isPresent() && questionModel.isPresent()) {
            Optional<QuizModel> quizModel = quizRepository.findById(questionModel.get().getQuizId());
            if(quizModel.isPresent()) {
                if(quizModel.get().getUserId().equals(userModel.get().getId()) || userModel.get().getRole().equals(UserRoleEnum.ADMIN)) {
                    return;
                }
                else {
                    throw new ResponseStatusException("Permission Denied", HttpStatus.FORBIDDEN);
                }
            }else {
                throw new ResponseStatusException("Quiz not found", HttpStatus.NOT_FOUND);
            }
        } else {
            throw new ResponseStatusException("Question/User not found", HttpStatus.NOT_FOUND);
        }
    }
}
