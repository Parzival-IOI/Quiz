package com.group1.quiz.enums;

import lombok.Getter;

@Getter
public enum QuizOrderEnum {
    ID("id"),
    NAME("name");

    private final String value;
    QuizOrderEnum(String value) {
        this.value = value;
    }
}
