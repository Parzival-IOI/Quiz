package com.group1.quiz.enums;

import lombok.Getter;

@Getter
public enum PlayOrderByEnum {
    NAME("name"),
    DATE("createdAt");

    private final String value;
    PlayOrderByEnum(String value) {
        this.value = value;
    }
}
