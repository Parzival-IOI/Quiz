package com.group1.quiz.service;

import com.group1.quiz.dataTransferObject.questionDTO.CreateQuestionRequest;
import com.group1.quiz.dataTransferObject.questionDTO.UpdateQuestionRequest;
import com.group1.quiz.model.QuestionModel;
import com.group1.quiz.repository.QuestionRepository;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public void createQuestion(CreateQuestionRequest createQuestionRequest) throws Exception {
        QuestionModel questionModel = QuestionModel.builder()
                .question(createQuestionRequest.getQuestion())
                .type(createQuestionRequest.getType())
                .quizId(createQuestionRequest.getQuizId())
                .createdAt(Date.from(Instant.now()))
                .updatedAt(Date.from(Instant.now()))
                .build();
        questionRepository.insert(questionModel);
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
            throw new Exception("Question not found");
        }
    }

    public void deleteQuestion(String id) throws Exception {
        Optional<QuestionModel> questionModel = questionRepository.findById(id);
        if (questionModel.isPresent()) {
            questionRepository.deleteById(id);
        }
        else {
            throw new Exception("Question not found");
        }
    }
}
