package com.group1.quiz.controller;

import com.group1.quiz.dataTransferObject.questionDTO.CreateQuestionRequest;
import com.group1.quiz.dataTransferObject.questionDTO.UpdateQuestionRequest;
import com.group1.quiz.service.QuestionService;
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
@RequestMapping("/api/question")
@RequiredArgsConstructor
@Slf4j
public class QuestionController {
    private final QuestionService questionService;
    @PostMapping("/create")
    public ResponseEntity<?> createQuestion(@RequestBody CreateQuestionRequest createQuestionRequest) {
        try {
            questionService.createQuestion(createQuestionRequest);
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable String id, @RequestBody UpdateQuestionRequest updateQuestionRequest) {
        try {
            questionService.updateQuestion(id, updateQuestionRequest);
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable String id) {
        try {
            questionService.deleteQuestion(id);
        } catch(Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

}
