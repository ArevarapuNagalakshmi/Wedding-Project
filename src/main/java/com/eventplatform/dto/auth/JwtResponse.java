package com.eventplatform.dto.auth;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    private String token;

    // Always "Bearer"
    private String tokenType = "Bearer";

    // Expiry time
    private long expiresAt;

    // 🔥 ADD THIS
    private String role;

    // Custom constructor
    public JwtResponse(String token, long expiresAt, String role) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.role = role;
        this.tokenType = "Bearer";
    }
}
