package com.eventplatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_verification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String aadhaarNumber;

    // ✅ ADD THIS FIELD (IMPORTANT FIX)
   // private String phone;

    private String otp;

    private LocalDateTime expiryTime;

    private boolean verified;
}