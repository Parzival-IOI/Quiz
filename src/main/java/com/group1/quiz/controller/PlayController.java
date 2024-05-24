package com.group1.quiz.controller;


import com.group1.quiz.dataTransferObject.PlayDTO.PlayQuizRequest;
import com.group1.quiz.dataTransferObject.PlayDTO.PlayQuizResponse;
import com.group1.quiz.service.PlayService;
import com.group1.quiz.util.ResponseStatusException;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/play")
@Slf4j
@RequiredArgsConstructor
public class PlayController {
    private final PlayService playService;

    @GetMapping("/quiz/{id}")
    public ResponseEntity<?> playQuiz(@PathVariable String id) {
        PlayQuizResponse playQuizResponse;
        try {
            playQuizResponse = playService.playQuiz(id);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(playQuizResponse, HttpStatus.OK);
    }

    @PostMapping("/quiz/summit")
    public ResponseEntity<?> playQuizSummit(@RequestBody PlayQuizRequest playQuizRequest) {
        try {
            playService.playQuizSummit(playQuizRequest);
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
