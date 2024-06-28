package com.group1.quiz.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class OpenApiConfig {
    private final String moduleName;
    private final String apiVersion;

    /***
     *Add Description for Spring Fox
     */
    public OpenApiConfig(@Value("${spring.application.name}") String moduleName) {
        this.moduleName = moduleName;
        this.apiVersion = "1.0";
    }

    /***
     * Configuration for Authentication JWT Token on Spring Fox Open API
     * @return OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "JWT Bearer Auth";
        final String apiTitle = String.format("%s API", StringUtils.capitalize(moduleName));
        return new OpenAPI()
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(
                new Components()
                    .addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                            .name(securitySchemeName)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    )
            )
            .info(new Info().title(apiTitle).version(apiVersion));
    }
}