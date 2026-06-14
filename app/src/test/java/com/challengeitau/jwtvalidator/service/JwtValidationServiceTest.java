package com.challengeitau.jwtvalidator.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.challengeitau.jwtvalidator.validator.ClaimsValidator;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.Test;

class JwtValidationServiceTest {

    private final JwtValidationService jwtValidationService = new JwtValidationService(
            new JwtClaimsExtractor(new ObjectMapper()),
            new ClaimsValidator(List.of())
    );

    @Test
    void shouldReturnFalseWhenTokenIsBlank() {
        boolean valid = jwtValidationService.validate(" ");

        assertThat(valid).isFalse();
    }

    @Test
    void shouldReturnFalseWhenTokenHasLessThanThreeParts() {
        boolean valid = jwtValidationService.validate("header.payload");

        assertThat(valid).isFalse();
    }

    @Test
    void shouldReturnFalseWhenPayloadIsNotBase64Url() {
        boolean valid = jwtValidationService.validate("eyJhbGciOiJIUzI1NiJ9.invalid_payload.signature");

        assertThat(valid).isFalse();
    }

    @Test
    void shouldReturnFalseWhenPayloadIsNotJsonObject() {
        String token = encodeJson("{}") + "." + encodeJson("[]") + ".signature";

        boolean valid = jwtValidationService.validate(token);

        assertThat(valid).isFalse();
    }

    @Test
    void shouldReturnTrueWhenTokenStructureIsValid() {
        String token = encodeJson("{\"alg\":\"HS256\"}")
                + "."
                + encodeJson("{\"Role\":\"Admin\",\"Seed\":\"7841\",\"Name\":\"Toninho Araujo\"}")
                + ".signature";

        boolean valid = jwtValidationService.validate(token);

        assertThat(valid).isTrue();
    }

    private String encodeJson(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
