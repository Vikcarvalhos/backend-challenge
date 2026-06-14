package com.itau.jwtvalidator.validator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.jwtvalidator.domain.JwtClaims;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class JwtClaimsExtractor {

    private final ObjectMapper objectMapper;

    public JwtClaimsExtractor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Optional<JwtClaims> extract(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        String[] parts = token.split("\\.", -1);

        if (parts.length != 3 || hasBlankPart(parts)) {
            return Optional.empty();
        }

        if (!isJsonObject(parts[0])) {
            return Optional.empty();
        }

        try {
            byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
            Map<String, String> values = objectMapper.readValue(
                    new String(payload, StandardCharsets.UTF_8),
                    new TypeReference<>() {
                    }
            );

            return Optional.of(new JwtClaims(values));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private boolean hasBlankPart(String[] parts) {
        for (String part : parts) {
            if (part == null || part.isBlank()) {
                return true;
            }
        }

        return false;
    }

    private boolean isJsonObject(String encodedValue) {
        try {
            byte[] decodedValue = Base64.getUrlDecoder().decode(encodedValue);
            return objectMapper.readTree(new String(decodedValue, StandardCharsets.UTF_8)).isObject();
        } catch (Exception exception) {
            return false;
        }
    }
}
