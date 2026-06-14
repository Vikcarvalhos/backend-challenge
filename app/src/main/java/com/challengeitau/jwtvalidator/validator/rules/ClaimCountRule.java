package com.challengeitau.jwtvalidator.validator.rules;

import com.challengeitau.jwtvalidator.domain.JwtClaims;
import com.challengeitau.jwtvalidator.domain.ValidationResult;
import com.challengeitau.jwtvalidator.validator.ClaimRule;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ClaimCountRule implements ClaimRule {

    private static final Set<String> REQUIRED_CLAIMS = Set.of("Name", "Role", "Seed");

    @Override
    public ValidationResult validate(JwtClaims claims) {
        if (claims.values().size() != REQUIRED_CLAIMS.size()) {
            return ValidationResult.invalid("JWT must contain exactly 3 claims");
        }

        if (!claims.values().keySet().equals(REQUIRED_CLAIMS)) {
            return ValidationResult.invalid("JWT must contain only Name, Role and Seed claims");
        }

        return ValidationResult.success();
    }
}
