package com.itau.jwtvalidator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response da validação de JWT.")
public record JwtValidationResponse(
        @Schema(description = "Indica se o JWT é válido conforme as regras de negócio.", example = "true")
        boolean valid
) {
}
