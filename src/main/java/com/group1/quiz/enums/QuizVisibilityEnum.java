package com.group1.quiz.enums;

import lombok.Getter;

@Getter
public enum QuizVisibilityEnum {
    PUBLIC("PUBLIC"),
    PRIVATE("PRIVATE");

    private final String value;
    QuizVisibilityEnum(String value) {
        this.value = value;
    }
}
