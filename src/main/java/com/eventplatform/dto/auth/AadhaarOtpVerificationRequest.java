package com.eventplatform.dto.auth;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AadhaarOtpVerificationRequest {

    @NotBlank(message = "Aadhaar number required")
    @Pattern(regexp = "\\d{12}", message = "Aadhaar must be 12 digits")
    private String aadhaarNumber;

    @NotBlank(message = "OTP required")
    @Pattern(regexp = "\\d{4,6}", message = "OTP must be 4 to 6 digits")
    private String otp;

}
