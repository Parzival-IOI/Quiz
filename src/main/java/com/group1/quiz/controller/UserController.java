package com.group1.quiz.controller;

import com.group1.quiz.dataTransferObject.QuizDTO.QuizzesResponse;
import com.group1.quiz.dataTransferObject.TableResponse;
import com.group1.quiz.dataTransferObject.UserDTO.UserRequest;
import com.group1.quiz.dataTransferObject.UserDTO.UserResponse;
import com.group1.quiz.enums.OrderEnum;
import com.group1.quiz.enums.QuizOrderByEnum;
import com.group1.quiz.enums.RowLengthEnum;
import com.group1.quiz.enums.UserOrderByEnum;
import com.group1.quiz.service.UserService;
import com.group1.quiz.util.ResponseStatusException;
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
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/find/{id}")
    public ResponseEntity<?> findOne(@PathVariable String id) {
        final UserResponse userResponse;
        try {
            userResponse = userService.findOne(id);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll(@RequestParam(required=false) String search, @RequestParam UserOrderByEnum orderBy, @RequestParam OrderEnum order, @RequestParam int page, @RequestParam RowLengthEnum size) {
        TableResponse<UserResponse> tableResponse;
        try {
            tableResponse = userService.getUsers(orderBy, order, page, size.getValue(), search);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(tableResponse, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserRequest userDto) {
        try {
            log.info("Create user: {}", userDto.getUsername());
            userService.createUser(userDto);
        } catch (ResponseStatusException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), e.getCode());
        } catch (Exception e){
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody UserRequest userDto) {
        try {
            userService.updateUser(id, userDto);
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
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
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
