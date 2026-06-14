package com.itau.jwtvalidator.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "JWT Validator API",
                version = "1.0.0",
                description = "API para validar JWTs conforme as regras do desafio técnico."
        )
)
public class OpenApiConfig {
}
