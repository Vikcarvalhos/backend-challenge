package com.challengeitau.jwtvalidator.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "JWT Validator API",
                version = "1.0.0",
                description = """
                        API REST para validar JWTs conforme as regras do desafio tecnico.

                        Regras de validacao:
                        - O token deve possuir estrutura de JWT com tres partes.
                        - O payload deve ser decodificavel como JSON.
                        - O payload deve conter exatamente as claims Name, Role e Seed.
                        - Name nao pode conter numeros e deve ter no maximo 256 caracteres.
                        - Role deve ser Admin, Member ou External.
                        - Seed deve ser um numero primo.

                        Premissa: o desafio nao fornece chave secreta, chave publica ou JWKS.
                        Por isso, a aplicacao valida estrutura, payload e regras de negocio,
                        mas nao executa validacao criptografica da assinatura.
                        """
        ),
        tags = {
                @Tag(
                        name = "JWT Validation",
                        description = "Operacao para validar JWTs usando a massa oficial e as regras do desafio."
                )
        }
)
public class OpenApiConfig {
}
