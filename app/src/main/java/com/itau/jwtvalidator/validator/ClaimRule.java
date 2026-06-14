package com.itau.jwtvalidator.validator;

import com.itau.jwtvalidator.domain.JwtClaims;
import com.itau.jwtvalidator.domain.ValidationResult;

public interface ClaimRule {

    ValidationResult validate(JwtClaims claims);
}
