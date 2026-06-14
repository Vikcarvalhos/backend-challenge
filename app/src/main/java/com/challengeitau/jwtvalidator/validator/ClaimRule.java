package com.challengeitau.jwtvalidator.validator;

import com.challengeitau.jwtvalidator.domain.JwtClaims;
import com.challengeitau.jwtvalidator.domain.ValidationResult;

public interface ClaimRule {

    ValidationResult validate(JwtClaims claims);
}
