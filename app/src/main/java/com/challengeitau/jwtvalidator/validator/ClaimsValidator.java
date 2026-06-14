package com.challengeitau.jwtvalidator.validator;

import com.challengeitau.jwtvalidator.domain.JwtClaims;
import com.challengeitau.jwtvalidator.domain.ValidationResult;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ClaimsValidator {

    private final List<ClaimRule> rules;

    public ClaimsValidator(List<ClaimRule> rules) {
        this.rules = rules;
    }

    public ValidationResult validate(JwtClaims claims) {
        return rules.stream()
                .map(rule -> rule.validate(claims))
                .filter(result -> !result.valid())
                .findFirst()
                .orElse(ValidationResult.success());
    }
}
