package com.challengeitau.jwtvalidator.controller;

import com.challengeitau.jwtvalidator.dto.JwtValidationRequest;
import com.challengeitau.jwtvalidator.dto.JwtValidationResponse;
import com.challengeitau.jwtvalidator.service.JwtValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/jwt")
@Tag(name = "JWT Validation")
public class JwtValidationController {

    private final JwtValidationService jwtValidationService;

    public JwtValidationController(JwtValidationService jwtValidationService) {
        this.jwtValidationService = jwtValidationService;
    }

    @PostMapping("/validate")
    @Operation(
            summary = "Valida um JWT conforme o desafio",
            description = """
                    Recebe um JWT e retorna um boolean indicando se ele atende as regras do desafio.

                    Regras aplicadas:
                    - JWT com tres partes separadas por ponto.
                    - Header e payload decodificaveis.
                    - Exatamente tres claims: Name, Role e Seed.
                    - Name sem numeros e com no maximo 256 caracteres.
                    - Role limitada a Admin, Member ou External.
                    - Seed numerico e primo.

                    Tokens invalidos por regra de negocio retornam HTTP 200 com valid=false.
                    Requests malformados, como token ausente ou vazio, retornam HTTP 400.
                    A assinatura criptografica nao e validada porque o enunciado nao fornece chave ou JWKS.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Objeto contendo o JWT que sera validado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JwtValidationRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Caso 1 - valido",
                                            summary = "JWT valido: Role Admin, Seed primo e Name sem numeros",
                                            value = """
                                                    {
                                                      "token": "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05sIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Caso 2 - JWT invalido",
                                            summary = "JWT estruturalmente invalido",
                                            value = """
                                                    {
                                                      "token": "eyJhbGciOiJzI1NiJ9.dfsdfsfryJSr2xrIjoiQWRtaW4iLCJTZrkIjoiNzg0MSIsIk5hbrUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05fsdfsIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Caso 3 - Name com numero",
                                            summary = "JWT invalido porque a claim Name contem numero",
                                            value = """
                                                    {
                                                      "token": "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiRXh0ZXJuYWwiLCJTZWVkIjoiODgwMzciLCJOYW1lIjoiTTRyaWEgT2xpdmlhIn0.6YD73XWZYQSSMDf6H0i3-kylz1-TY_Yt6h1cV2Ku-Qs"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Caso 4 - Claim extra",
                                            summary = "JWT invalido porque contem mais de tres claims",
                                            value = """
                                                    {
                                                      "token": "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiTWVtYmVyIiwiT3JnIjoiQlIiLCJTZWVkIjoiMTQ2MjciLCJOYW1lIjoiVmFsZGlyIEFyYW5oYSJ9.cmrXV_Flm5mfdpfNUVopY_I2zeJUy4EZ4i3Fea98zvY"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "JWT processado com sucesso. O campo valid indica true para JWT valido e false para JWT invalido por regra de negocio.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JwtValidationResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "JWT valido",
                                                    summary = "Resultado para o caso oficial 1",
                                                    value = """
                                                            {
                                                              "valid": true
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "JWT invalido",
                                                    summary = "Resultado para os casos oficiais 2, 3 e 4",
                                                    value = """
                                                            {
                                                              "valid": false
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Request malformado, JSON invalido, campo token ausente ou token vazio."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Erro inesperado durante o processamento."
                    )
            }
    )
    public ResponseEntity<JwtValidationResponse> validate(@Valid @RequestBody JwtValidationRequest request) {
        boolean valid = jwtValidationService.validate(request.token());
        return ResponseEntity.ok(new JwtValidationResponse(valid));
    }
}
