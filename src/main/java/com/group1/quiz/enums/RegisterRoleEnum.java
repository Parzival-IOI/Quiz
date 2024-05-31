package com.group1.quiz.enums;

import lombok.Getter;

@Getter
public enum RegisterRoleEnum {
    TEACHER("TEACHER"),
    STUDENT("STUDENT");

    private final String value;
    RegisterRoleEnum(String value) {
        this.value = value;
    }
}
