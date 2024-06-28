package com.group1.quiz.config;


import com.group1.quiz.enums.UserRoleEnum;
import com.group1.quiz.service.UserImplementService;
import com.group1.quiz.service.UserService;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;


@Slf4j
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final RsaKeyProperties rsaKeyProperties;
    private final UserImplementService userService;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        //handle route authentication and authorization
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationManager(authenticationManager)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth", "/register", "/v2/register", "/authenticateEmail", "/resendOTP").permitAll()
                        .requestMatchers("/migrate").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/refreshToken").hasRole("REFRESH_TOKEN")
                        .requestMatchers("/api/user/**").hasRole(UserRoleEnum.ADMIN.getValue())
                        .requestMatchers("/api/mail/**").hasRole(UserRoleEnum.ADMIN.getValue())
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer((oauth2) -> oauth2
                        .jwt(
                                jwt -> jwt.jwtAuthenticationConverter(new CustomAuthenticationConverter())
                        )
                )
                .build();
    }

    /**
     * Custom JWT Authorization
     */
    static class CustomAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
        public AbstractAuthenticationToken convert(Jwt jwt) {
            Collection<String> authorities = jwt.getClaimAsStringList("role");
            String validation = "Validation : " + jwt.getSubject() + " " + jwt.getClaimAsStringList("role");
            log.info(validation);
            if(authorities == null || authorities.isEmpty()) {
                return new JwtAuthenticationToken(jwt);
            }
            Collection<GrantedAuthority> grantedAuthorities = authorities.stream().map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            return new JwtAuthenticationToken(jwt, grantedAuthorities);
        }
    }

    /**
     * Decoder For JWT
     * @return JWT Decoder
     */
    @Bean
    JwtDecoder jwtDecoder() throws Exception {
        return NimbusJwtDecoder.withPublicKey(rsaKeyProperties.getRSAPublickey()).build();
    }

    /**
     * Encoder For JWT
     * @return JWT Decoder
     */
    @Bean
    JwtEncoder jwtEncoder() throws Exception {
        JWK jwk = new RSAKey.Builder(rsaKeyProperties.getRSAPublickey()).privateKey(rsaKeyProperties.getRSAPrivatekey()).build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }

}