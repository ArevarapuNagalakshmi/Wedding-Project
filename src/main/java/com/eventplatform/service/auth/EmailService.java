package com.eventplatform.service.auth;


public interface EmailService {

    void sendOtpEmail(String toEmail, String otp);
}

