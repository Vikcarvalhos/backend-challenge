package com.itau.jwtvalidator.validator.rules;

import com.itau.jwtvalidator.domain.JwtClaims;
import com.itau.jwtvalidator.domain.ValidationResult;
import com.itau.jwtvalidator.validator.ClaimRule;
import org.springframework.stereotype.Component;

@Component
public class NameRule implements ClaimRule {

    private static final String NAME_CLAIM = "Name";
    private static final int MAX_NAME_LENGTH = 256;

    @Override
    public ValidationResult validate(JwtClaims claims) {
        return claims.getAsString(NAME_CLAIM)
                .map(this::validateName)
                .orElseGet(() -> ValidationResult.invalid("Name claim must be a string"));
    }

    private ValidationResult validateName(String name) {
        if (name.isBlank()) {
            return ValidationResult.invalid("Name claim must not be blank");
        }

        if (name.length() > MAX_NAME_LENGTH) {
            return ValidationResult.invalid("Name claim exceeds maximum length");
        }

        if (containsDigit(name)) {
            return ValidationResult.invalid("Name claim contains numeric characters");
        }

        return ValidationResult.success();
    }

    private boolean containsDigit(String value) {
        return value.chars().anyMatch(Character::isDigit);
    }
}
