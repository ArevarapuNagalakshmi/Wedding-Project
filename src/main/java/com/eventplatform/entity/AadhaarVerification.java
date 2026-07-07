package com.eventplatform.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "aadhaar_verification")
public class AadhaarVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Aadhaar number (12 digits)
    @Column(nullable = false, unique = true, length = 12)
    private String aadhaarNumber;


    // Mobile number linked to Aadhaar
    @Column(nullable = false, length = 10)
    private String mobileNumber;


    // Optional: Name as per Aadhaar
    @Column(nullable = false)
    private String fullName;


    // Optional: verification status
    @Column(nullable = false)
    private boolean active;

}
