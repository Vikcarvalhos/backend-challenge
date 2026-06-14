package com.itau.jwtvalidator.validator.rules;

import com.itau.jwtvalidator.domain.JwtClaims;
import com.itau.jwtvalidator.domain.ValidationResult;
import com.itau.jwtvalidator.validator.ClaimRule;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class RoleRule implements ClaimRule {

    private static final String ROLE_CLAIM = "Role";
    private static final Set<String> ALLOWED_ROLES = Set.of("Admin", "Member", "External");

    @Override
    public ValidationResult validate(JwtClaims claims) {
        return claims.getAsString(ROLE_CLAIM)
                .map(this::validateRole)
                .orElseGet(() -> ValidationResult.invalid("Role claim must be a string"));
    }

    private ValidationResult validateRole(String role) {
        if (!ALLOWED_ROLES.contains(role)) {
            return ValidationResult.invalid("Role claim is not allowed");
        }

        return ValidationResult.success();
    }
}
