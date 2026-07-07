package com.eventplatform.controller;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/verify")
@PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
public class VerificationController {


    // INITIATE AADHAAR VERIFICATION (OTP trigger)
    @PostMapping("/aadhaar/initiate")
    public ResponseEntity<Map<String, String>> initiateAadhaar(
            @Valid @RequestBody AadhaarInitiateRequest request) {

        // TODO: integrate with Aadhaar / third-party KYC provider

        return ResponseEntity.ok(
                Map.of(
                        "status", "initiated",
                        "referenceId", "SIMULATED_REF_123"
                )
        );
    }


    // VALIDATE AADHAAR OTP
    @PostMapping("/aadhaar/validate")
    public ResponseEntity<Map<String, String>> validateAadhaar(
            @Valid @RequestBody AadhaarValidateRequest request) {

        // TODO: verify OTP with provider

        return ResponseEntity.ok(
                Map.of("status", "validated")
        );
    }


    // REQUEST DTOs
    // (can later be moved to dto package)
    @Data
    static class AadhaarInitiateRequest {

        @NotBlank(message = "Aadhaar number is required")
        private String aadhaarNumber;
    }

    @Data
    static class AadhaarValidateRequest {

        @NotBlank(message = "Reference ID is required")
        private String referenceId;

        @NotBlank(message = "OTP is required")
        private String otp;
    }
}
