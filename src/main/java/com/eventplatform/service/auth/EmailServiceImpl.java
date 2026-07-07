package com.eventplatform.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        // ✅ VERY IMPORTANT (add this)
        message.setFrom("nagalakshmiarevarapu@gmail.com");

        message.setTo(toEmail);

        message.setSubject("Wedding Platform - Email Verification OTP");

        message.setText(
                "Dear User,\n\n" +
                        "Your OTP is: " + otp + "\n\n" +
                        "This OTP is valid for 5 minutes.\n\n" +
                        "Regards,\nWedding Platform Team"
        );

        mailSender.send(message);
    }
}
