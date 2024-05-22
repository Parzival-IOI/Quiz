package com.group1.quiz.controller;

import com.group1.quiz.dataTransferObject.quizDTO.CreateQuizRequest;
import com.group1.quiz.dataTransferObject.quizDTO.QuizOrderByRequest;
import com.group1.quiz.dataTransferObject.quizDTO.QuizResponse;
import com.group1.quiz.dataTransferObject.quizDTO.QuizzesResponse;
import com.group1.quiz.service.QuizService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Slf4j
public class QuizController {
    private final QuizService quizService;

    @GetMapping("/get-self-quiz")
    public ResponseEntity<?> GetSelfQuiz(Principal principal) {
        List<QuizzesResponse> quizModels;
        try {
            quizModels = quizService.getSelfQuiz(principal);
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (quizModels.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(quizModels, HttpStatus.OK);
    }

    @GetMapping("/get-all/{orderBy}/{page}/{size}/{search}")
    public ResponseEntity<?> GetQuizzes(@PathVariable QuizOrderByRequest orderBy, @PathVariable int page, @PathVariable int size, @PathVariable String search) {
        List<QuizzesResponse> quizzesResponses;
        try {
            quizzesResponses = quizService.getQuizzes(orderBy, page, size, search);
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(quizzesResponses, HttpStatus.OK);
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<?> GetQuizById(@PathVariable String id) {
        QuizResponse quizResponse;
        try {
            quizResponse = quizService.getQuizById(id);
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(quizResponse, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createQuiz(@RequestBody CreateQuizRequest createQuizRequest, Principal principal) {
        try {
            quizService.createQuiz(createQuizRequest, principal);
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }
}
