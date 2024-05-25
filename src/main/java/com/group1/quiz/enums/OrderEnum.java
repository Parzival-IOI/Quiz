package com.group1.quiz.enums;

import lombok.Getter;

@Getter
public enum OrderEnum {
    ASC("ASC"),
    DESC("DESC");

    private final String value;
    OrderEnum(String value) {
        this.value = value;
    }
}
