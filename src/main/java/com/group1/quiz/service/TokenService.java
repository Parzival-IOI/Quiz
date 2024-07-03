package com.group1.quiz.service;

import com.group1.quiz.dataTransferObject.AuthResponse;
import com.group1.quiz.dataTransferObject.LoginRequest;
import com.group1.quiz.model.BlockedUserModel;
import com.group1.quiz.model.LoginModel;
import com.group1.quiz.model.UserModel;
import com.group1.quiz.repository.BlockedUserRepository;
import com.group1.quiz.repository.LoginRepository;
import com.group1.quiz.repository.UserRepository;
import com.group1.quiz.util.ResponseStatusException;
import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final LoginRepository loginRepository;
    private final BlockedUserRepository blockedUserRepository;

    public AuthResponse generateToken(LoginRequest loginRequest) throws  Exception {
        Optional<UserModel> user = userRepository.findUserByUsername(loginRequest.username());
        if(user.isPresent()) {
            if(!user.get().getPassword().equals(loginRequest.password())) {
                Optional<BlockedUserModel> blockedUserModel = blockedUserRepository.findByUsername(user.get().getUsername());
                if(blockedUserModel.isPresent()) {
                    int attempts = blockedUserModel.get().getAttempt();
                    if(attempts > 5) {
                        throw new ResponseStatusException("Blocked", HttpStatus.UNAUTHORIZED);
                    } else {
                        blockedUserModel.get().setAttempt(attempts + 1);
                        blockedUserRepository.save(blockedUserModel.get());
                    }
                } else {
                    blockedUserRepository.insert(
                            BlockedUserModel.builder()
                                    .username(user.get().getUsername())
                                    .attempt(1)
                                    .build()
                    );
                }
            }
            else {
                Optional<BlockedUserModel> blockedUserModel = blockedUserRepository.findByUsername(user.get().getUsername());
                blockedUserModel.ifPresent(userModel -> blockedUserRepository.deleteById(userModel.getId()));
            }
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        Instant now = Instant.now();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        //access token
        JwtClaimsSet accessToken = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(15*60))
                .subject(authentication.getName())
                .claim("role", role)
                .build();

        //refresh token
        JwtClaimsSet refreshToken = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("role", "ROLE_REFRESH_TOKEN")
                .claim("token", "refresh")
                .build();

        String generatedAccessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(accessToken)).getTokenValue();
        String generatedRefreshToken = this.jwtEncoder.encode(JwtEncoderParameters.from(refreshToken)).getTokenValue();

        String createdToken = "Login : " + authentication.getName() + "/" + role + "/" + generatedAccessToken;
        log.info(createdToken);

        Optional<LoginModel> loginModel = loginRepository.findByUserName(authentication.getName());
        if(loginModel.isPresent()) {
            loginModel.get().setRefreshToken(generatedRefreshToken);
            loginRepository.save(loginModel.get());
        } else {
            loginRepository.insert(
                    LoginModel.builder()
                            .userName(authentication.getName())
                            .refreshToken(generatedRefreshToken)
                            .build()
            );
        }

        return AuthResponse.builder()
                .accessToken(generatedAccessToken)
                .refreshToken(generatedRefreshToken)
                .build();
    }

    public AuthResponse generateRefreshToken(Principal principal, Jwt jwt) throws Exception {
        Optional<UserModel> userModel = userRepository.findUserByUsername(principal.getName());
        if(userModel.isPresent()) {

            Optional<LoginModel> loginModel = loginRepository.findByUserName(userModel.get().getUsername());
            if(loginModel.isPresent()) {
                if(!loginModel.get().getRefreshToken().equals(jwt.getTokenValue())) {
                    throw new ResponseStatusException("invalid", HttpStatus.BAD_REQUEST);
                }
            }
            else {
                throw new ResponseStatusException("invalid", HttpStatus.BAD_REQUEST);
            }

            Instant now = Instant.now();
            String role = userModel.get().getRole().getValue();
            //access token
            JwtClaimsSet accessToken = JwtClaimsSet.builder()
                    .issuer("self")
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(15*60))
                    .subject(userModel.get().getUsername())
                    .claim("role", "ROLE_" + role)
                    .build();

            //refresh token
            JwtClaimsSet refreshToken = JwtClaimsSet.builder()
                    .issuer("self")
                    .issuedAt(now)
                    .expiresAt(now.plus(1, ChronoUnit.HOURS))
                    .subject(userModel.get().getUsername())
                    .claim("role", "ROLE_REFRESH_TOKEN")
                    .claim("token", "refresh")
                    .build();

            String generatedAccessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(accessToken)).getTokenValue();
            String generatedRefreshToken = this.jwtEncoder.encode(JwtEncoderParameters.from(refreshToken)).getTokenValue();

            if(loginModel.isPresent()) {
                loginModel.get().setRefreshToken(generatedRefreshToken);
                loginRepository.save(loginModel.get());
            }

            String createdToken = "refresh : " + userModel.get().getUsername() + "/" + role + "/" + generatedAccessToken;
            log.info(createdToken);

            return AuthResponse.builder()
                    .accessToken(generatedAccessToken)
                    .refreshToken(generatedRefreshToken)
                    .build();
        }
        throw new ResponseStatusException("Invalid", HttpStatus.BAD_REQUEST);
    }
}
