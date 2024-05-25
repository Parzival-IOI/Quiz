package com.group1.quiz.service;

import com.group1.quiz.dataTransferObject.AnswerDTO.CreateAnswerRequest;
import com.group1.quiz.dataTransferObject.AnswerDTO.UpdateAnswerRequest;
import com.group1.quiz.model.AnswerModel;
import com.group1.quiz.repository.AnswerRepository;
import com.group1.quiz.util.ResponseStatusException;
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
            throw new ResponseStatusException("Answer not found", HttpStatus.NOT_FOUND);
        }
    }

    public void deleteAnswer(String id) throws Exception {
        if (answerRepository.existsById(id)) {
            answerRepository.deleteById(id);
        }
        else {
            throw new ResponseStatusException("Answer not found", HttpStatus.NOT_FOUND);
        }
    }
}
