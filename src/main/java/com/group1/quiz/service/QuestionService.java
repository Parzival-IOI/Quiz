package com.group1.quiz.service;

import com.group1.quiz.dataTransferObject.AnswerDTO.CreateAnswerWithQuestion;
import com.group1.quiz.dataTransferObject.QuestionDTO.CreateQuestionAndAnswerRequest;
import com.group1.quiz.dataTransferObject.QuestionDTO.CreateQuestionRequest;
import com.group1.quiz.dataTransferObject.QuestionDTO.UpdateQuestionRequest;
import com.group1.quiz.model.AnswerModel;
import com.group1.quiz.model.QuestionModel;
import com.group1.quiz.model.QuizModel;
import com.group1.quiz.repository.AnswerRepository;
import com.group1.quiz.repository.QuestionRepository;
import com.group1.quiz.repository.QuizRepository;
import com.group1.quiz.util.ResponseStatusException;
import java.time.Instant;
import java.util.Date;
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

    public QuestionModel createQuestion(CreateQuestionRequest createQuestionRequest) throws Exception {
        boolean isQuiz = quizRepository.existsById(createQuestionRequest.getQuizId());
        if (isQuiz) {
            QuestionModel questionModel = QuestionModel.builder()
                    .question(createQuestionRequest.getQuestion())
                    .type(createQuestionRequest.getType())
                    .quizId(createQuestionRequest.getQuizId())
                    .createdAt(Date.from(Instant.now()))
                    .updatedAt(Date.from(Instant.now()))
                    .build();
            questionRepository.insert(questionModel);
            return questionModel;
        }
        throw new ResponseStatusException("Quiz Not Found", HttpStatus.NOT_FOUND);
    }

    public void updateQuestion(String id, UpdateQuestionRequest updateQuestionRequest) throws Exception {
        Optional<QuestionModel> questionModel = questionRepository.findById(id);
        if (questionModel.isPresent()) {
            QuestionModel question = QuestionModel.builder()
                    .id(questionModel.get().getId())
                    .question(updateQuestionRequest.getQuestion())
                    .type(updateQuestionRequest.getType())
                    .quizId(questionModel.get().getQuizId())
                    .createdAt(questionModel.get().getCreatedAt())
                    .updatedAt(Date.from(Instant.now()))
                    .build();
            questionRepository.save(question);
        }
        else {
            throw new ResponseStatusException("Question not found", HttpStatus.NOT_FOUND);
        }
    }

    public void deleteQuestion(String id) throws Exception {
        if (questionRepository.existsById(id)) {
            questionRepository.deleteById(id);
        }
        else {
            throw new ResponseStatusException("Question not found", HttpStatus.NOT_FOUND);
        }
    }

    public void createQuestionAndAnswer(CreateQuestionAndAnswerRequest createQuestionAndAnswerRequest) throws Exception {
        boolean isQuizExist = quizRepository.existsById(createQuestionAndAnswerRequest.getQuizId());
        if (isQuizExist) {
            QuestionModel questionModel = QuestionModel.builder()
                    .question(createQuestionAndAnswerRequest.getQuestion())
                    .type(createQuestionAndAnswerRequest.getType())
                    .quizId(createQuestionAndAnswerRequest.getQuizId())
                    .createdAt(Date.from(Instant.now()))
                    .updatedAt(Date.from(Instant.now()))
                    .build();
            questionRepository.insert(questionModel);

            for(CreateAnswerWithQuestion createAnswerWithQuestion : createQuestionAndAnswerRequest.getAnswer()) {
                AnswerModel answerModel = AnswerModel.builder()
                        .answer(createAnswerWithQuestion.getAnswer())
                        .isCorrect(createAnswerWithQuestion.isCorrect())
                        .questionId(questionModel.getId())
                        .build();
                answerRepository.insert(answerModel);
            }
        }
        else {
            throw new ResponseStatusException("Quiz Not Found", HttpStatus.NOT_FOUND);
        }
    }
}
