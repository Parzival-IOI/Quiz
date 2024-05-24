package com.group1.quiz.service;

import com.group1.quiz.dataTransferObject.answerDTO.CreateAnswerRequest;
import com.group1.quiz.dataTransferObject.answerDTO.UpdateAnswerRequest;
import com.group1.quiz.model.AnswerModel;
import com.group1.quiz.repository.AnswerRepository;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;

    public void createAnswer(CreateAnswerRequest createAnswerRequest) throws Exception {
        AnswerModel answerModel = AnswerModel.builder()
                .answer(createAnswerRequest.getAnswer())
                .isCorrect(createAnswerRequest.isCorrect())
                .questionId(createAnswerRequest.getQuestionId())
                .createdAt(Date.from(Instant.now()))
                .updatedAt(Date.from(Instant.now()))
                .build();
        answerRepository.insert(answerModel);
    }

    public void updateAnswer(String id, UpdateAnswerRequest updateAnswerRequest) throws Exception {
        Optional<AnswerModel> answerModel = answerRepository.findById(id);
        if (answerModel.isPresent()) {
            AnswerModel answer = AnswerModel.builder()
                    .id(answerModel.get().getId())
                    .answer(updateAnswerRequest.getAnswer())
                    .isCorrect(updateAnswerRequest.isCorrect())
                    .questionId(answerModel.get().getQuestionId())
                    .createdAt(answerModel.get().getCreatedAt())
                    .updatedAt(Date.from(Instant.now()))
                    .build();
            answerRepository.save(answer);
        }
        else {
            throw new Exception("Answer not found");
        }
    }

    public void deleteAnswer(String id) throws Exception {
        Optional<AnswerModel> answerModel = answerRepository.findById(id);
        if (answerModel.isPresent()) {
            answerRepository.deleteById(id);
        }
        else {
            throw new Exception("Answer not found");
        }
    }
}
