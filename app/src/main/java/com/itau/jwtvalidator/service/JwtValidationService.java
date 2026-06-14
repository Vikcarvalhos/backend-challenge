package com.itau.jwtvalidator.service;

import com.itau.jwtvalidator.domain.JwtClaims;
import com.itau.jwtvalidator.validator.ClaimsValidator;
import com.itau.jwtvalidator.validator.JwtClaimsExtractor;
import com.itau.jwtvalidator.validator.ValidationResult;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JwtValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtValidationService.class);

    private final JwtClaimsExtractor jwtClaimsExtractor;
    private final ClaimsValidator claimsValidator;

    public JwtValidationService(JwtClaimsExtractor jwtClaimsExtractor, ClaimsValidator claimsValidator) {
        this.jwtClaimsExtractor = jwtClaimsExtractor;
        this.claimsValidator = claimsValidator;
    }

    public boolean validate(String token) {
        LOGGER.info("Received JWT validation request");

        Optional<JwtClaims> claims = jwtClaimsExtractor.extract(token);

        if (claims.isEmpty()) {
            LOGGER.warn("Invalid JWT structure");
            return false;
        }

        ValidationResult validationResult = claimsValidator.validate(claims.get());

        if (!validationResult.valid()) {
            LOGGER.warn("Invalid JWT claims");
            return false;
        }

        LOGGER.info("JWT validated successfully");
        return true;
    }
}
