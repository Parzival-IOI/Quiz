package com.group1.quiz.controller;

import com.group1.quiz.dataTransferObject.QuizDTO.UpdateQuizRequest;
import com.group1.quiz.enums.RowLengthEnum;
import com.group1.quiz.dataTransferObject.QuizDTO.CreateQuizRequest;
import com.group1.quiz.enums.QuizOrderEnum;
import com.group1.quiz.dataTransferObject.QuizDTO.QuizResponse;
import com.group1.quiz.dataTransferObject.QuizDTO.QuizTableResponse;
import com.group1.quiz.dataTransferObject.QuizDTO.QuizzesResponse;
import com.group1.quiz.service.QuizService;
import com.group1.quiz.util.ResponseStatusException;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Slf4j
public class QuizController {
    private final QuizService quizService;

    @GetMapping("/myQuiz")
    public ResponseEntity<?> getSelfQuiz(Principal principal) {
        List<QuizzesResponse> quizModels;
        try {
            quizModels = quizService.getSelfQuiz(principal);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (quizModels.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(quizModels, HttpStatus.OK);
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> getQuizzes(@RequestParam(required=false) String search, @RequestParam QuizOrderEnum orderBy, @RequestParam int page, @RequestParam RowLengthEnum size) {
        QuizTableResponse quizzesResponses;
        try {
            quizzesResponses = quizService.getQuizzes(orderBy, page, size.getValue(), search);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(quizzesResponses, HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable String id, Principal principal) {
        QuizResponse quizResponse;
        try {
            quizResponse = quizService.getQuizById(id, principal);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
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
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateQuiz(@PathVariable String id, @RequestBody UpdateQuizRequest updateQuizRequest) {
        try {
            quizService.updateQuiz(id, updateQuizRequest);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch(Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable String id) {
        try {
            quizService.deleteQuiz(id);
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
