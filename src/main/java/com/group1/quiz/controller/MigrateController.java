package com.group1.quiz.controller;

import com.group1.quiz.service.MigrateService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/migrate")
@RequiredArgsConstructor
@Slf4j
public class MigrateController {
    private final MigrateService migrateService;
    @GetMapping("/")
    public String migrate() {
        try {
            migrateService.migrate();
        } catch (Exception e) {
            log.info(e.getMessage());
            return e.getMessage();
        }
        return "create user";
    }
}
