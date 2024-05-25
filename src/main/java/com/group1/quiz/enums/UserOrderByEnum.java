package com.group1.quiz.enums;

import lombok.Getter;

@Getter
public enum UserOrderByEnum {
    NAME("name"),
    ROLE("role"),
    DATE("created_at");

    private final String value;
    UserOrderByEnum(String value) {
        this.value = value;
    }
}
