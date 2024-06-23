package com.group1.quiz.controller;


import com.group1.quiz.dataTransferObject.PlayDTO.PlayQuizRequest;
import com.group1.quiz.dataTransferObject.PlayDTO.PlayQuizResponse;
import com.group1.quiz.dataTransferObject.PlayDTO.PlayResponse;
import com.group1.quiz.dataTransferObject.PlayDTO.PlaySubmitResponse;
import com.group1.quiz.dataTransferObject.PlayDTO.PlaysResponse;
import com.group1.quiz.dataTransferObject.TableResponse;
import com.group1.quiz.dataTransferObject.QuizDTO.QuizzesResponse;
import com.group1.quiz.enums.OrderEnum;
import com.group1.quiz.enums.PlayOrderByEnum;
import com.group1.quiz.enums.QuizOrderByEnum;
import com.group1.quiz.enums.RowLengthEnum;
import com.group1.quiz.service.PlayService;
import com.group1.quiz.util.ResponseStatusException;
import java.security.Principal;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/find/{id}")
    public ResponseEntity<?> findPlay(@PathVariable String id, Principal principal) {
        PlayResponse playResponse;
        try {
            playResponse = playService.findPlay(id, principal);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(playResponse, HttpStatus.OK);
    }

    @GetMapping("/findAllAdmin")
    public ResponseEntity<?> getPlays(@RequestParam(required=false) String search, @RequestParam PlayOrderByEnum orderBy, @RequestParam OrderEnum order, @RequestParam int page, @RequestParam RowLengthEnum size, Principal principal) {
        TableResponse<PlaysResponse> tableResponse;
        try {
            tableResponse = playService.getPlaysAdmin(orderBy, order, page, size.getValue(), search, principal);
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
    public ResponseEntity<?> getPlaysSelf(@RequestParam(required=false) String search, @RequestParam PlayOrderByEnum orderBy, @RequestParam OrderEnum order, @RequestParam int page, @RequestParam RowLengthEnum size, Principal principal) {
        TableResponse<PlaysResponse> tableResponse;
        try {
            tableResponse = playService.getPlays(orderBy, order, page, size.getValue(), search, principal);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(tableResponse, HttpStatus.OK);
    }

    @PostMapping("/quiz/summit")
    public ResponseEntity<?> playQuizSummit(@RequestBody PlayQuizRequest playQuizRequest, Principal principal) {
        PlaySubmitResponse playSubmitResponse;
        try {
            playSubmitResponse = playService.playQuizSummit(playQuizRequest, principal);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage() + Arrays.toString(e.getStackTrace()), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(playSubmitResponse, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePlay(@PathVariable String id, Principal principal) {
        try {
            playService.deletePlay(id , principal);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage() + Arrays.toString(e.getStackTrace()), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }


}
