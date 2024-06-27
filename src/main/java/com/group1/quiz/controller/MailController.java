package com.group1.quiz.controller;

import com.group1.quiz.service.MailService;
import com.group1.quiz.util.OTPGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {
    private final MailService mailService;

    @GetMapping("/{to}/{subject}/{text}")
    public void sendMail(@PathVariable String to, @PathVariable String subject, @PathVariable String text) {
        mailService.sendMail(to, subject, text);
    }
}
