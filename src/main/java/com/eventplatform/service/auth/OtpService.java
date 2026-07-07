package com.eventplatform.service.auth;

public interface OtpService {

    // ================= EMAIL OTP =================

    void sendEmailOtp(String email);

    void verifyEmailOtp(String email, String otp);

    boolean isEmailVerified(String email);


    // ================= AADHAAR OTP =================

    void sendOtpByAadhaar(String aadhaarNumber);

    void verifyOtpByAadhaar(String aadhaarNumber, String otp);

    boolean isAadhaarVerified(String aadhaarNumber);
}