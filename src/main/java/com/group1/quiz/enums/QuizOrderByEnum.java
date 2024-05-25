package com.group1.quiz.enums;

import lombok.Getter;

@Getter
public enum QuizOrderByEnum {
    NAME("name"),
    DATE("createdAt");

    private final String value;
    QuizOrderByEnum(String value) {
        this.value = value;
    }
}
