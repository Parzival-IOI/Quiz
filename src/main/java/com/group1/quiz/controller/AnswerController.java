package com.group1.quiz.controller;

import com.group1.quiz.dataTransferObject.AnswerDTO.CreateAnswerRequest;
import com.group1.quiz.dataTransferObject.AnswerDTO.UpdateAnswerRequest;
import com.group1.quiz.model.AnswerModel;
import com.group1.quiz.service.AnswerService;
import com.group1.quiz.util.ResponseStatusException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
public class AnswerController {
    private final AnswerService answerService;

    @PostMapping("/create")
    public ResponseEntity<?> createAnswer(@RequestBody CreateAnswerRequest createAnswerRequest, Principal principal) {
        AnswerModel answerModel;
        try {
            answerModel = answerService.createAnswer(createAnswerRequest, principal);
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
    public ResponseEntity<?> updateAnswer(@PathVariable String id, @RequestBody UpdateAnswerRequest updateAnswerRequest, Principal principal) {
        try {
            answerService.updateAnswer(id, updateAnswerRequest, principal);
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
    public ResponseEntity<?> deleteAnswer(@PathVariable String id, Principal principal) {
        try {
            answerService.deleteAnswer(id, principal);
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
