package com.eventplatform.service.auth;

import com.eventplatform.config.JwtUtil;
import com.eventplatform.dto.auth.JwtResponse;
import com.eventplatform.dto.auth.LoginRequest;
import com.eventplatform.dto.auth.PasswordResetRequest;
import com.eventplatform.dto.auth.RegisterRequest;
import com.eventplatform.entity.AadhaarVerification;
import com.eventplatform.entity.Role;
import com.eventplatform.entity.User;
import com.eventplatform.repository.AadhaarVerificationRepository;
import com.eventplatform.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AadhaarVerificationRepository aadhaarRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    // ================= REGISTER =================

    @Override
    @Transactional
    public void register(RegisterRequest req) {

        // Email already exists
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Email OTP verified
        if (!otpService.isEmailVerified(req.getEmail())) {
            throw new RuntimeException("Email not verified");
        }

        // Aadhaar exists
        AadhaarVerification aadhaar =
                aadhaarRepository.findByAadhaarNumber(req.getAadhaarNumber())
                        .orElseThrow(() ->
                                new RuntimeException("Invalid Aadhaar Number"));

        //Aadhaar OTP verified
        if (!otpService.isAadhaarVerified(req.getAadhaarNumber())) {
            throw new RuntimeException("Mobile not verified");
        }

        // Mobile from Aadhaar
        String mobileNumber = aadhaar.getMobileNumber();

        //Mobile already exists
        if (userRepository.findByPhone(mobileNumber).isPresent()) {
            throw new RuntimeException("Mobile already registered");
        }

        //Default role
        Role role = req.getRole() == null
                ? Role.CUSTOMER
                : req.getRole();

        // ✅ Create user
        User user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .aadhaarNumber(req.getAadhaarNumber())
                .phone(mobileNumber)
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(role)
                .emailVerified(true)
                .phoneVerified(true)
                .build();

        userRepository.save(user);
    }

    // ================= LOGIN =================

    @Override
    public JwtResponse login(LoginRequest req) {

        User user = userRepository
                .findByEmail(req.getEmailOrPhone())
                .or(() -> userRepository.findByPhone(req.getEmailOrPhone()))
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        req.getPassword()
                )
        );

        String token = jwtUtil.generateToken(
                user.getEmail(),
                Map.of(
                        "id", user.getId(),
                        "role", user.getRole().name()
                )
        );

        return new JwtResponse(
                token,
                jwtUtil.getClaims(token).getExpiration().getTime(),
                user.getRole().name()
        );
    }

    @Override
    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }
        if (request.getOtp() == null || request.getOtp().isBlank()) {
            throw new RuntimeException("OTP is required");
        }
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new RuntimeException("New password is required");
        }

        otpService.verifyEmailOtp(request.getEmail(), request.getOtp());

        User user = userRepository.findByEmail(request.getEmail().trim())
                .orElseThrow(() -> new RuntimeException("User not found with this email"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}