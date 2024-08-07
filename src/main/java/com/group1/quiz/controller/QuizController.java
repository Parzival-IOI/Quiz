package com.group1.quiz.controller;

import com.group1.quiz.dataTransferObject.PlayDTO.PlaysPlayerResponse;
import com.group1.quiz.dataTransferObject.PlayDTO.PlaysResponse;
import com.group1.quiz.dataTransferObject.QuizDTO.UpdateQuizRequest;
import com.group1.quiz.dataTransferObject.QuizDTO.UpdateQuizRequest2;
import com.group1.quiz.enums.OrderEnum;
import com.group1.quiz.enums.PlayOrderByEnum;
import com.group1.quiz.enums.RowLengthEnum;
import com.group1.quiz.dataTransferObject.QuizDTO.CreateQuizRequest;
import com.group1.quiz.enums.QuizOrderByEnum;
import com.group1.quiz.dataTransferObject.QuizDTO.QuizResponse;
import com.group1.quiz.dataTransferObject.TableResponse;
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

    @GetMapping("/v2/myQuiz")
    public ResponseEntity<?> getSelfQuiz2(@RequestParam(required=false) String search, @RequestParam QuizOrderByEnum orderBy, @RequestParam OrderEnum order, @RequestParam int page, @RequestParam RowLengthEnum size, Principal principal) {
        TableResponse<QuizzesResponse> tableResponse;
        try {
            tableResponse = quizService.getSelfQuiz2(orderBy, order, page, size.getValue(), search, principal);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(tableResponse, HttpStatus.OK);
    }

    @GetMapping("/myQuiz/player")
    public ResponseEntity<?> getSelfQuizPlayer(@RequestParam(required=false) String search, @RequestParam PlayOrderByEnum orderBy, @RequestParam OrderEnum order, @RequestParam int page, @RequestParam RowLengthEnum size, @RequestParam String quizId, Principal principal) {
        TableResponse<PlaysPlayerResponse> tableResponse;
        try {
            tableResponse = quizService.getSelfQuizPlayer(orderBy, order, page, size.getValue(), search, quizId, principal);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(tableResponse, HttpStatus.OK);
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> getQuizzes(@RequestParam(required=false) String search, @RequestParam QuizOrderByEnum orderBy, @RequestParam OrderEnum order, @RequestParam int page, @RequestParam RowLengthEnum size) {
        TableResponse<QuizzesResponse> tableResponse;
        try {
            tableResponse = quizService.getQuizzes(orderBy, order, page, size.getValue(), search);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(tableResponse, HttpStatus.OK);
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
        QuizResponse quizResponse;
        try {
            quizResponse = quizService.createQuiz(createQuizRequest, principal);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(quizResponse, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateQuiz(@PathVariable String id, @RequestBody UpdateQuizRequest updateQuizRequest, Principal principal) {
        try {
            quizService.updateQuiz(id, updateQuizRequest, principal);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch(Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @PutMapping("/v2/update/{id}")
    public ResponseEntity<?> updateQuiz2(@PathVariable String id, @RequestBody UpdateQuizRequest2 updateQuizRequest2, Principal principal) {
        try {
            quizService.updateQuiz2(id, updateQuizRequest2, principal);
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
    public ResponseEntity<?> deleteQuiz(@PathVariable String id, Principal principal) {
        try {
            quizService.deleteQuiz(id, principal);
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
