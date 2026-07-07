package com.eventplatform.controller;

import com.eventplatform.dto.auth.*;
import com.eventplatform.dto.auth.PasswordResetRequest;
import com.eventplatform.entity.AadhaarVerification;
import com.eventplatform.repository.AadhaarVerificationRepository;
import com.eventplatform.service.auth.AuthService;
import com.eventplatform.service.auth.OtpService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;
    private final AadhaarVerificationRepository aadhaarRepository;

    // ================= EMAIL OTP =================

    @PostMapping("/send-email-otp")
    public ResponseEntity<String> sendEmailOtp(
            @Valid @RequestBody EmailOtpRequest request) {

        otpService.sendEmailOtp(request.getEmail());

        return ResponseEntity.ok(
                "Email OTP sent successfully"
        );
    }

    @PostMapping("/verify-email-otp")
    public ResponseEntity<String> verifyEmailOtp(
            @Valid @RequestBody OtpVerificationRequest request) {

        otpService.verifyEmailOtp(
                request.getEmail(),
                request.getOtp()
        );

        return ResponseEntity.ok(
                "Email verified successfully"
        );
    }

    // ================= REGISTER AADHAAR =================

    @PostMapping("/register-aadhaar")
    public ResponseEntity<String> registerAadhaar(
            @Valid @RequestBody AadhaarRegistrationRequest request) {

        String aadhaar =
                request.getAadhaarNumber().trim();

        String mobile =
                request.getMobileNumber().trim();

        String fullName =
                request.getFullName().trim();

        // Aadhaar validation
        if (!aadhaar.matches("\\d{12}")) {

            return ResponseEntity
                    .badRequest()
                    .body("Aadhaar number must contain 12 digits");
        }

        // Mobile validation
        if (!mobile.matches("\\d{10}")) {

            return ResponseEntity
                    .badRequest()
                    .body("Mobile number must contain 10 digits");
        }

        // Check existing Aadhaar
        if (aadhaarRepository
                .findByAadhaarNumber(aadhaar)
                .isPresent()) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Aadhaar already registered");
        }

        AadhaarVerification aadhaarData =
                AadhaarVerification.builder()
                        .aadhaarNumber(aadhaar)
                        .fullName(fullName)
                        .mobileNumber(mobile)
                        .active(false)
                        .build();

        aadhaarRepository.save(aadhaarData);

        return ResponseEntity.ok(
                "Aadhaar registered successfully"
        );
    }

    // ================= SEND AADHAAR OTP =================

    @PostMapping("/send-aadhaar-otp")
    public ResponseEntity<String> sendAadhaarOtp(
            @Valid @RequestBody AadhaarOtpRequest request) {

        String aadhaar =
                request.getAadhaarNumber().trim();

        System.out.println(
                "Aadhaar received: " + aadhaar
        );

        otpService.sendOtpByAadhaar(aadhaar);

        return ResponseEntity.ok(
                "OTP sent successfully"
        );
    }

    // ================= VERIFY AADHAAR OTP =================

    @PostMapping("/verify-aadhaar-otp")
    public ResponseEntity<String> verifyAadhaarOtp(
            @Valid @RequestBody AadhaarOtpVerificationRequest request) {

        otpService.verifyOtpByAadhaar(
                request.getAadhaarNumber(),
                request.getOtp()
        );

        return ResponseEntity.ok(
                "Aadhaar verified successfully"
        );
    }

    // ================= REGISTER USER =================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {

        boolean isVerified =
                otpService.isAadhaarVerified(
                        request.getAadhaarNumber()
                );

        if (!isVerified) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Please verify Aadhaar before registration"
                    );
        }

        authService.register(request);

        return ResponseEntity.ok(
                "Registered successfully. Please login."
        );
    }

    // ================= LOGIN =================

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request) {

        JwtResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }

    // ================= RESET PASSWORD =================

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody PasswordResetRequest request) {

        authService.resetPassword(request);

        return ResponseEntity.ok(
                "Password reset successfully. You can now login with your new password."
        );
    }
}