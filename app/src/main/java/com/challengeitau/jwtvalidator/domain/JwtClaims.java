package com.challengeitau.jwtvalidator.domain;

import java.util.Map;
import java.util.Optional;

public record JwtClaims(Map<String, Object> values) {

    public Optional<String> getAsString(String claimName) {
        Object value = values.get(claimName);

        if (value instanceof String stringValue) {
            return Optional.of(stringValue);
        }

        return Optional.empty();
    }
}
