package com.eventplatform.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AadhaarRegistrationRequest {

    @NotBlank(message = "Aadhaar number required")
    @Pattern(regexp = "\\d{12}", message = "Aadhaar must be 12 digits")
    private String aadhaarNumber;

    @NotBlank(message = "Full name required")
    private String fullName;

    @NotBlank(message = "Mobile number required")
    @Pattern(regexp = "\\d{10}", message = "Mobile number must be 10 digits")
    private String mobileNumber;
}