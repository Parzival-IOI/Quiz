package com.group1.quiz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
public class testController {
    @GetMapping("/")
    public String hello() {
        return "Hello World";
    }
}
