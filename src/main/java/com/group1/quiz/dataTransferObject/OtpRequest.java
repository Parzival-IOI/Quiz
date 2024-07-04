package com.group1.quiz.dataTransferObject;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OtpRequest {
    private String otp;
    private String email;
}
