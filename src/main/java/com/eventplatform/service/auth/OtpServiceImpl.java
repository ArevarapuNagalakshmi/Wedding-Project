package com.eventplatform.service.auth;

import com.eventplatform.entity.AadhaarVerification;
import com.eventplatform.entity.OtpVerification;
import com.eventplatform.repository.AadhaarVerificationRepository;
import com.eventplatform.repository.OtpVerificationRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class OtpServiceImpl implements OtpService {

    private final OtpVerificationRepository otpRepository;
    private final AadhaarVerificationRepository aadhaarRepository;
    private final EmailService emailService;

    private static final int OTP_EXPIRY_MINUTES = 5;

    private final SecureRandom secureRandom = new SecureRandom();

    // ================= GENERATE OTP =================

    private String generateOtp() {

        return String.valueOf(
                100000 + secureRandom.nextInt(900000)
        );
    }

    // ================= EMAIL OTP =================

    @Override
    public void sendEmailOtp(String email) {

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email cannot be empty");
        }

        email = email.trim();

        // delete old OTP
        otpRepository.findTopByEmailOrderByIdDesc(email)
                .ifPresent(otpRepository::delete);

        String otp = generateOtp();

        OtpVerification otpVerification = OtpVerification.builder()
                .email(email)
                .otp(otp)
                .expiryTime(
                        LocalDateTime.now()
                                .plusMinutes(OTP_EXPIRY_MINUTES)
                )
                .verified(false)
                .build();

        otpRepository.save(otpVerification);

        emailService.sendOtpEmail(email, otp);

        System.out.println("================================");
        System.out.println("EMAIL OTP SENT");
        System.out.println("Email: " + email);
        System.out.println("OTP: " + otp);
        System.out.println("================================");
    }

    @Override
    public void verifyEmailOtp(String email, String otp) {

        if (email == null || otp == null) {
            throw new RuntimeException("Email and OTP required");
        }

        email = email.trim();
        otp = otp.trim();

        OtpVerification record = otpRepository
                .findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() ->
                        new RuntimeException("OTP not found"));

        // check expiry
        if (record.getExpiryTime()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException("OTP expired");
        }

        // validate OTP
        if (!record.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        record.setVerified(true);
        record.setOtp(null);

        otpRepository.save(record);

        System.out.println("Email verified successfully");
    }

    @Override
    public boolean isEmailVerified(String email) {

        if (email == null || email.isBlank()) {
            return false;
        }

        return otpRepository
                .findTopByEmailOrderByIdDesc(email.trim())
                .map(OtpVerification::isVerified)
                .orElse(false);
    }

    // ================= AADHAAR OTP =================

    @Override
    public void sendOtpByAadhaar(String aadhaarNumber) {

        if (aadhaarNumber == null || aadhaarNumber.isBlank()) {
            throw new RuntimeException("Aadhaar number cannot be empty");
        }

        String aadhaar = aadhaarNumber.trim();

        // Aadhaar validation
        if (!aadhaar.matches("\\d{12}")) {
            throw new RuntimeException(
                    "Aadhaar number must contain 12 digits"
            );
        }

        AadhaarVerification data = aadhaarRepository
                .findByAadhaarNumber(aadhaar)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Aadhaar not registered. Please register first."
                        ));

        String mobileNumber = data.getMobileNumber();

        if (mobileNumber == null || mobileNumber.isBlank()) {
            throw new RuntimeException("Mobile number not linked");
        }

        // Mobile validation
        if (!mobileNumber.matches("\\d{10}")) {
            throw new RuntimeException("Invalid mobile number");
        }

        // delete old OTP
        otpRepository.findTopByAadhaarNumberOrderByIdDesc(aadhaar)
                .ifPresent(otpRepository::delete);

        String otp = generateOtp();

        OtpVerification otpVerification = OtpVerification.builder()
                .aadhaarNumber(aadhaar)
                .otp(otp)
                .expiryTime(
                        LocalDateTime.now()
                                .plusMinutes(OTP_EXPIRY_MINUTES)
                )
                .verified(false)
                .build();

        otpRepository.save(otpVerification);

        sendOtpToMobile(mobileNumber, otp);
    }

    @Override
    public void verifyOtpByAadhaar(String aadhaarNumber, String otp) {

        if (aadhaarNumber == null || otp == null) {
            throw new RuntimeException(
                    "Aadhaar number and OTP required"
            );
        }

        String aadhaar = aadhaarNumber.trim();
        otp = otp.trim();

        OtpVerification record = otpRepository
                .findTopByAadhaarNumberOrderByIdDesc(aadhaar)
                .orElseThrow(() ->
                        new RuntimeException("OTP not found"));

        // check expiry
        if (record.getExpiryTime()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException("OTP expired");
        }

        // validate OTP
        if (!record.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        // mark OTP verified
        record.setVerified(true);
        record.setOtp(null);

        otpRepository.save(record);

        // activate Aadhaar
        AadhaarVerification aadhaarData = aadhaarRepository
                .findByAadhaarNumber(aadhaar)
                .orElseThrow(() ->
                        new RuntimeException("Aadhaar not found"));

        aadhaarData.setActive(true);

        aadhaarRepository.save(aadhaarData);

        System.out.println("Aadhaar verified successfully");
    }

    // ================= CHECK VERIFIED =================

    @Override
    public boolean isAadhaarVerified(String aadhaarNumber) {

        if (aadhaarNumber == null ||
                aadhaarNumber.isBlank()) {

            return false;
        }

        return aadhaarRepository
                .findByAadhaarNumber(aadhaarNumber.trim())
                .map(AadhaarVerification::isActive)
                .orElse(false);
    }

    // ================= MOCK SMS =================

    private void sendOtpToMobile(
            String mobileNumber,
            String otp
    ) {

        System.out.println("================================");
        System.out.println("OTP SENT TO MOBILE");
        System.out.println("Mobile Number: " + mobileNumber);
        System.out.println("OTP: " + otp);
        System.out.println("================================");
    }
}