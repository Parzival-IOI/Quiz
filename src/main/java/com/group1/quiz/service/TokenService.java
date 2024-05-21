package com.group1.quiz.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {
    private final JwtEncoder jwtEncoder;
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("role", role)
                .build();
        String generatedToken = this.jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
        String createdToken = "Login : " + authentication.getName() + "/" + role + "/" + generatedToken;
        log.info(createdToken);
        return generatedToken;
    }

}
