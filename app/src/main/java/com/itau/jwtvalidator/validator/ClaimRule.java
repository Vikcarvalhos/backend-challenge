package com.itau.jwtvalidator.validator;

import com.itau.jwtvalidator.domain.JwtClaims;

public interface ClaimRule {

    ValidationResult validate(JwtClaims claims);
}
