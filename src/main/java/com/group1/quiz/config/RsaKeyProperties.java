package com.group1.quiz.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rsa")
public record RsaKeyProperties(String rsaPublicKey, String rsaPrivateKey) {
}
