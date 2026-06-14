package com.challengeitau.jwtvalidator.validator.rules;

import static org.assertj.core.api.Assertions.assertThat;

import com.challengeitau.jwtvalidator.domain.JwtClaims;
import com.challengeitau.jwtvalidator.domain.ValidationResult;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RoleRuleTest {

    private final RoleRule rule = new RoleRule();

    @Test
    void shouldReturnValidWhenRoleIsAllowed() {
        assertThat(rule.validate(new JwtClaims(Map.of("Role", "Admin"))).valid()).isTrue();
        assertThat(rule.validate(new JwtClaims(Map.of("Role", "Member"))).valid()).isTrue();
        assertThat(rule.validate(new JwtClaims(Map.of("Role", "External"))).valid()).isTrue();
    }

    @Test
    void shouldReturnInvalidWhenRoleIsNotAllowed() {
        ValidationResult result = rule.validate(new JwtClaims(Map.of("Role", "Owner")));

        assertThat(result.valid()).isFalse();
    }

    @Test
    void shouldReturnInvalidWhenRoleIsNotString() {
        ValidationResult result = rule.validate(new JwtClaims(Map.of("Role", 123)));

        assertThat(result.valid()).isFalse();
    }
}
