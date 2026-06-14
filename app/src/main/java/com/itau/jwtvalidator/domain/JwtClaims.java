package com.itau.jwtvalidator.domain;

import java.util.Map;

public record JwtClaims(Map<String, String> values) {
}
