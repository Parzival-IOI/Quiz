package com.group1.quiz.config;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rsa")
public record RsaKeyProperties(String rsaPublicKey, String rsaPrivateKey) {
    public RSAPublicKey getRSAPublickey() throws Exception {
        byte[] data = Base64.getDecoder().decode((this.rsaPublicKey.getBytes()));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) fact.generatePublic(spec);
    }
    public RSAPrivateKey getRSAPrivatekey() throws Exception {
        byte[] clear = Base64.getDecoder().decode(this.rsaPrivateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return (RSAPrivateKey) privateKey;
    }
}
