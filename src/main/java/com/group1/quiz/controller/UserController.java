package com.group1.quiz.controller;

import com.group1.quiz.dataTransferObject.UserDto;
import com.group1.quiz.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    @PostMapping("/create")
    public void createUser(@RequestBody UserDto userDto) {
        log.info("Create user: {}", userDto.getUsername());
        this.userService.createUser(userDto);
    }
}
