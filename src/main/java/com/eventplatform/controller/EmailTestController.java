package com.eventplatform.controller;

import com.eventplatform.service.auth.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class EmailTestController {

    private final EmailService emailService;

    @GetMapping("/send-email")
    public String sendTestEmail(@RequestParam String email) {

        String otp = "123456";

        emailService.sendOtpEmail(email, otp);

        return "Test email sent successfully";
    }
}
