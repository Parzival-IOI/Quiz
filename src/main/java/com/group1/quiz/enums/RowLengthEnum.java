package com.group1.quiz.enums;

import lombok.Getter;

@Getter
public enum RowLengthEnum {
    TEN(10),
    FIFTEEN(15),
    TWENTY(20);

    private final int value;
    RowLengthEnum(int value){
        this.value = value;
    }
}
