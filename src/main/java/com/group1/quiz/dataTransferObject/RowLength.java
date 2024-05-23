package com.group1.quiz.dataTransferObject;

import lombok.Getter;

@Getter
public enum RowLength {
    TEN(10),
    FIFTEEN(15),
    TWENTY(20);

    private final int value;
    RowLength(int value){
        this.value = value;
    }
}
