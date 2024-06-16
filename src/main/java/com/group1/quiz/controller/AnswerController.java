package com.group1.quiz.controller;

import com.group1.quiz.dataTransferObject.AnswerDTO.CreateAnswerRequest;
import com.group1.quiz.dataTransferObject.AnswerDTO.UpdateAnswerRequest;
import com.group1.quiz.model.AnswerModel;
import com.group1.quiz.service.AnswerService;
import com.group1.quiz.util.ResponseStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/answer")
@RequiredArgsConstructor
@Slf4j
public class AnswerController {
    private final AnswerService answerService;

    @PostMapping("/create")
    public ResponseEntity<?> createAnswer(@RequestBody CreateAnswerRequest createAnswerRequest) {
        AnswerModel answerModel;
        try {
            answerModel = answerService.createAnswer(createAnswerRequest);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(answerModel, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAnswer(@PathVariable String id, @RequestBody UpdateAnswerRequest updateAnswerRequest) {
        try {
            answerService.updateAnswer(id, updateAnswerRequest);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAnswer(@PathVariable String id) {
        try {
            answerService.deleteAnswer(id);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
