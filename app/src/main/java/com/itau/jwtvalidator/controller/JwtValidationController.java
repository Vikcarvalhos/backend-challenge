package com.itau.jwtvalidator.controller;

import com.itau.jwtvalidator.dto.JwtValidationRequest;
import com.itau.jwtvalidator.dto.JwtValidationResponse;
import com.itau.jwtvalidator.service.JwtValidationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/jwt")
public class JwtValidationController {

    private final JwtValidationService jwtValidationService;

    public JwtValidationController(JwtValidationService jwtValidationService) {
        this.jwtValidationService = jwtValidationService;
    }

    @PostMapping("/validate")
    public ResponseEntity<JwtValidationResponse> validate(@Valid @RequestBody JwtValidationRequest request) {
        boolean valid = jwtValidationService.validate(request.token());
        return ResponseEntity.ok(new JwtValidationResponse(valid));
    }
}
