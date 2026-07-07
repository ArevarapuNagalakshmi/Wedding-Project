package com.eventplatform.repository;

import com.eventplatform.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByEmailOrderByIdDesc(String email);

    // NEW METHOD
    Optional<OtpVerification> findTopByAadhaarNumberOrderByIdDesc(String aadhaarNumber);

}
