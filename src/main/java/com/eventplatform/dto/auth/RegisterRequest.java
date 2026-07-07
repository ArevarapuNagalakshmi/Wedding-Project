package com.eventplatform.dto.auth;

import com.eventplatform.entity.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "\\d{12}",
            message = "Aadhaar must be 12 digits")
    @NotBlank(message = "Aadhaar number is required")
    private String aadhaarNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 8)
    private String password;

    @NotNull(message = "Role is required")
    private Role role;
}
