package com.itau.jwtvalidator.controller;

import com.itau.jwtvalidator.dto.JwtValidationRequest;
import com.itau.jwtvalidator.dto.JwtValidationResponse;
import com.itau.jwtvalidator.service.JwtValidationService;
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
@Tag(name = "JWT Validation", description = "Operações para validação de JWT.")
public class JwtValidationController {

    private final JwtValidationService jwtValidationService;

    public JwtValidationController(JwtValidationService jwtValidationService) {
        this.jwtValidationService = jwtValidationService;
    }

    @PostMapping("/validate")
    @Operation(
            summary = "Valida um JWT",
            description = "Recebe um JWT e retorna se ele é válido conforme as regras do desafio.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Token JWT que será validado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JwtValidationRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "JWT válido",
                                            value = """
                                                    {
                                                      "token": "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05sIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "JWT inválido",
                                            value = """
                                                    {
                                                      "token": "eyJhbGciOiJzI1NiJ9.dfsdfsfryJSr2xrIjoiQWRtaW4iLCJTZrkIjoiNzg0MSIsIk5hbrUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05fsdfsIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "JWT processado com sucesso. O campo valid indica o resultado da validação.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JwtValidationResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "JWT válido",
                                                    value = """
                                                            {
                                                              "valid": true
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "JWT inválido",
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
                            description = "Request malformado ou token ausente/vazio."
                    ),
                    @ApiResponse(responseCode = "500", description = "Erro inesperado.")
            }
    )
    public ResponseEntity<JwtValidationResponse> validate(@Valid @RequestBody JwtValidationRequest request) {
        boolean valid = jwtValidationService.validate(request.token());
        return ResponseEntity.ok(new JwtValidationResponse(valid));
    }
}
