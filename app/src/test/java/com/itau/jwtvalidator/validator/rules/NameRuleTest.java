package com.itau.jwtvalidator.validator.rules;

import static org.assertj.core.api.Assertions.assertThat;

import com.itau.jwtvalidator.domain.JwtClaims;
import com.itau.jwtvalidator.domain.ValidationResult;
import java.util.Map;
import org.junit.jupiter.api.Test;

class NameRuleTest {

    private final NameRule rule = new NameRule();

    @Test
    void shouldReturnValidWhenNameHasNoNumericCharacters() {
        ValidationResult result = rule.validate(new JwtClaims(Map.of("Name", "Toninho Araujo")));

        assertThat(result.valid()).isTrue();
    }

    @Test
    void shouldReturnInvalidWhenNameContainsNumericCharacters() {
        ValidationResult result = rule.validate(new JwtClaims(Map.of("Name", "M4ria Olivia")));

        assertThat(result.valid()).isFalse();
    }

    @Test
    void shouldReturnInvalidWhenNameExceedsMaximumLength() {
        ValidationResult result = rule.validate(new JwtClaims(Map.of("Name", "A".repeat(257))));

        assertThat(result.valid()).isFalse();
    }

    @Test
    void shouldReturnInvalidWhenNameIsNotString() {
        ValidationResult result = rule.validate(new JwtClaims(Map.of("Name", 123)));

        assertThat(result.valid()).isFalse();
    }
}
