package com.group1.quiz.enums;

import lombok.Getter;

@Getter
public enum UserRoleEnum {
    ADMIN("ADMIN"),
    TEACHER("TEACHER"),
    STUDENT("STUDENT");

    private final String value;
    UserRoleEnum(String value) {
        this.value = value;
    }
}
