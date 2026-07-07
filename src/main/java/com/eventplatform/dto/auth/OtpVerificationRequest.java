package com.eventplatform.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpVerificationRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String otp;
}
