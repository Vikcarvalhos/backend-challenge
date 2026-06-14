package com.itau.jwtvalidator.validator.rules;

import static org.assertj.core.api.Assertions.assertThat;

import com.itau.jwtvalidator.domain.JwtClaims;
import com.itau.jwtvalidator.domain.ValidationResult;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ClaimCountRuleTest {

    private final ClaimCountRule rule = new ClaimCountRule();

    @Test
    void shouldReturnValidWhenClaimsContainOnlyRequiredClaims() {
        ValidationResult result = rule.validate(new JwtClaims(Map.of(
                "Name", "Toninho Araujo",
                "Role", "Admin",
                "Seed", "7841"
        )));

        assertThat(result.valid()).isTrue();
    }

    @Test
    void shouldReturnInvalidWhenClaimsContainExtraClaim() {
        ValidationResult result = rule.validate(new JwtClaims(Map.of(
                "Name", "Toninho Araujo",
                "Role", "Admin",
                "Seed", "7841",
                "Org", "BR"
        )));

        assertThat(result.valid()).isFalse();
    }

    @Test
    void shouldReturnInvalidWhenRequiredClaimIsMissing() {
        ValidationResult result = rule.validate(new JwtClaims(Map.of(
                "Name", "Toninho Araujo",
                "Role", "Admin"
        )));

        assertThat(result.valid()).isFalse();
    }
}
