package com.group1.quiz.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ResponseStatusException extends Exception {
    // Add returned Code. Ex: 400, 401, 403, 500 etc.
    private HttpStatus code;

    /**
     * Add custom code for the Exception
     * @param message String
     * @param code HttpStatus
     */
    public ResponseStatusException(String message, HttpStatus code) {
        super(message);
        this.code = code;
    }
}
