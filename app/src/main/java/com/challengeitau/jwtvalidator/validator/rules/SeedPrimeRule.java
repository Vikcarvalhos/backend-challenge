package com.challengeitau.jwtvalidator.validator.rules;

import com.challengeitau.jwtvalidator.domain.JwtClaims;
import com.challengeitau.jwtvalidator.domain.ValidationResult;
import com.challengeitau.jwtvalidator.validator.ClaimRule;
import org.springframework.stereotype.Component;

@Component
public class SeedPrimeRule implements ClaimRule {

    private static final String SEED_CLAIM = "Seed";

    @Override
    public ValidationResult validate(JwtClaims claims) {
        return claims.getAsString(SEED_CLAIM)
                .map(this::validateSeed)
                .orElseGet(() -> ValidationResult.invalid("Seed claim must be a string"));
    }

    private ValidationResult validateSeed(String seed) {
        try {
            int value = Integer.parseInt(seed);

            if (!isPrime(value)) {
                return ValidationResult.invalid("Seed claim must be a prime number");
            }

            return ValidationResult.success();
        } catch (NumberFormatException exception) {
            return ValidationResult.invalid("Seed claim must be numeric");
        }
    }

    private boolean isPrime(int value) {
        if (value <= 1) {
            return false;
        }

        if (value == 2) {
            return true;
        }

        if (value % 2 == 0) {
            return false;
        }

        for (int divisor = 3; divisor <= Math.sqrt(value); divisor += 2) {
            if (value % divisor == 0) {
                return false;
            }
        }

        return true;
    }
}
