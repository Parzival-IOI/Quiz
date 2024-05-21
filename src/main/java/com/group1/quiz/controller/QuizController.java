package com.group1.quiz.controller;

import com.group1.quiz.dataTransferObject.CreateQuizRequest;
import com.group1.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @PostMapping("/create")
    public String createQuiz(@RequestBody CreateQuizRequest createQuizRequest) {
        try {
            quizService.createQuiz(createQuizRequest);
        } catch (Exception e) {
            log.info(e.getMessage());
            return e.getMessage();
        }
        return "Successful";
    }
}
