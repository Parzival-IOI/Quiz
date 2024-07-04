package com.group1.quiz.enums;

import lombok.Getter;

@Getter
public enum UserOrderByEnum {
    NAME("username"),
    ROLE("role"),
    DATE("createdAt");

    private final String value;
    UserOrderByEnum(String value) {
        this.value = value;
    }
}
