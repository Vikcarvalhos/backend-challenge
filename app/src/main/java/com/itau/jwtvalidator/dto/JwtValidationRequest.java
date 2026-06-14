package com.itau.jwtvalidator.dto;

import jakarta.validation.constraints.NotBlank;

public record JwtValidationRequest(@NotBlank String token) {
}
