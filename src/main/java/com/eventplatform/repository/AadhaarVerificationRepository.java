package com.eventplatform.repository;

import com.eventplatform.entity.AadhaarVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AadhaarVerificationRepository
        extends JpaRepository<AadhaarVerification, Long> {

    Optional<AadhaarVerification> findByAadhaarNumber(String aadhaarNumber);

}
