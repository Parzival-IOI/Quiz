package com.group1.quiz.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ResponseStatusException extends Exception {
    private HttpStatus code;
    public ResponseStatusException(String message, HttpStatus code) {
        super(message);
        this.code = code;
    }
}
