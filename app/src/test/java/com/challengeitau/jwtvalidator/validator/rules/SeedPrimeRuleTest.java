package com.challengeitau.jwtvalidator.validator.rules;

import static org.assertj.core.api.Assertions.assertThat;

import com.challengeitau.jwtvalidator.domain.JwtClaims;
import com.challengeitau.jwtvalidator.domain.ValidationResult;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SeedPrimeRuleTest {

    private final SeedPrimeRule rule = new SeedPrimeRule();

    @Test
    void shouldReturnValidWhenSeedIsPrime() {
        ValidationResult result = rule.validate(new JwtClaims(Map.of("Seed", "7841")));

        assertThat(result.valid()).isTrue();
    }

    @Test
    void shouldReturnInvalidWhenSeedIsNotPrime() {
        ValidationResult result = rule.validate(new JwtClaims(Map.of("Seed", "7840")));

        assertThat(result.valid()).isFalse();
    }

    @Test
    void shouldReturnInvalidWhenSeedIsNotNumeric() {
        ValidationResult result = rule.validate(new JwtClaims(Map.of("Seed", "abc")));

        assertThat(result.valid()).isFalse();
    }

    @Test
    void shouldReturnInvalidWhenSeedIsNotString() {
        ValidationResult result = rule.validate(new JwtClaims(Map.of("Seed", 7841)));

        assertThat(result.valid()).isFalse();
    }
}
