package com.group1.quiz.util;

import java.util.Random;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
public class OTPGenerator {

    private final static Integer LENGTH = 6;

    public static Supplier<String> generate() {
        return () -> {
            Random random = new Random();
            StringBuilder oneTimePassword = new StringBuilder();
            for (int i = 0; i < LENGTH; i++) {
                int randomNumber = random.nextInt(10);
                oneTimePassword.append(randomNumber);
            }
            return oneTimePassword.toString().trim();
        };
    }
}
