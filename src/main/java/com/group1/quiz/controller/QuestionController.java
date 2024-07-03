package com.group1.quiz.controller;

import com.group1.quiz.dataTransferObject.QuestionDTO.CreateQuestionAndAnswerRequest;
import com.group1.quiz.dataTransferObject.QuestionDTO.CreateQuestionRequest;
import com.group1.quiz.dataTransferObject.QuestionDTO.QuestionResponse;
import com.group1.quiz.dataTransferObject.QuestionDTO.UpdateQNARequest;
import com.group1.quiz.dataTransferObject.QuestionDTO.UpdateQuestionRequest;
import com.group1.quiz.model.QuestionModel;
import com.group1.quiz.service.QuestionService;
import com.group1.quiz.util.ResponseStatusException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/findQNA/{id}")
    public ResponseEntity<?> findQNA(@PathVariable String id, Principal principal) {
        QuestionResponse questionResponse;
        try {
            questionResponse = questionService.findQNA(id, principal);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(questionResponse, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createQuestion(@RequestBody CreateQuestionRequest createQuestionRequest, Principal principal) {
        QuestionModel questionModel;
        try {
            questionModel = questionService.createQuestion(createQuestionRequest, principal);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(questionModel, HttpStatus.OK);
    }

    @PostMapping("/createQNA")
    public ResponseEntity<?> createQuestionAndAnswer(@RequestBody CreateQuestionAndAnswerRequest createQuestionAndAnswerRequest, Principal principal) {
        try {
            questionService.createQuestionAndAnswer(createQuestionAndAnswerRequest, principal);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable String id, @RequestBody UpdateQuestionRequest updateQuestionRequest, Principal principal) {
        try {
            questionService.updateQuestion(id, updateQuestionRequest, principal);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @PutMapping("/updateQNA/{id}")
    public ResponseEntity<?> updateQNA(@PathVariable String id, @RequestBody UpdateQNARequest updateQNARequest, Principal principal) {
        try {
            questionService.updateQNA(id, updateQNARequest, principal);
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
    public ResponseEntity<?> deleteQuestion(@PathVariable String id, Principal principal) {
        try {
            questionService.deleteQuestion(id, principal);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch(Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

}
