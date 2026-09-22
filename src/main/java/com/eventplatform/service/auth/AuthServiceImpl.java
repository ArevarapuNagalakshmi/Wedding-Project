package com.eventplatform.service.auth;

import com.eventplatform.config.JwtUtil;
import com.eventplatform.dto.auth.JwtResponse;
import com.eventplatform.dto.auth.LoginRequest;
import com.eventplatform.dto.auth.PasswordResetRequest;
import com.eventplatform.dto.auth.RegisterRequest;
import com.eventplatform.entity.AadhaarVerification;
import com.eventplatform.entity.Role;
import com.eventplatform.entity.User;
import com.eventplatform.entity.VendorProfile;
import com.eventplatform.repository.AadhaarVerificationRepository;
import com.eventplatform.repository.UserRepository;
import com.eventplatform.repository.VendorProfileRepository;

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
    private final VendorProfileRepository vendorProfileRepository;
    private final AadhaarVerificationRepository aadhaarRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    // ================= REGISTER =================

    @Override
    @Transactional
    public void register(RegisterRequest req) {

        // Email already exists (case-insensitive)
        if (userRepository.existsByEmailIgnoreCase(req.getEmail().trim())) {
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
        // Ensure the user is persisted to database immediately
        userRepository.flush();

        if (role == Role.VENDOR) {
            VendorProfile vendorProfile = VendorProfile.builder()
                    .owner(user)
                    .businessName((req.getFirstName() + " " + req.getLastName()).trim())
                    .category("Wedding Services")
                    .categoryTags(new java.util.ArrayList<>())
                    .imageUrls(new java.util.ArrayList<>())
                    .videoUrls(new java.util.ArrayList<>())
                    .rating(0.0)
                    .verified(false)
                    .build();

            vendorProfileRepository.save(vendorProfile);
            vendorProfileRepository.flush();
        }
    }

    // ================= LOGIN =================

    @Override
    public JwtResponse login(LoginRequest req) {

        if (req.getEmailOrPhone() == null || req.getEmailOrPhone().isBlank()) {
            throw new RuntimeException("Email or phone is required");
        }

        String identifier = req.getEmailOrPhone().trim();

        // Try case-insensitive email lookup first
        var userOpt = userRepository.findByEmailIgnoreCase(identifier);

        // If not an email match, try phone lookup after normalizing digits
        if (userOpt.isEmpty()) {
            String digits = identifier.replaceAll("\\D+", "");
            // strip leading country code '91' if present
            if (digits.length() == 12 && digits.startsWith("91")) {
                digits = digits.substring(2);
            }

            if (digits.length() == 10) {
                userOpt = userRepository.findByPhone(digits);
            }
        }

        User user = userOpt.orElseThrow(() ->
                new RuntimeException("Invalid email or password"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        req.getPassword()
                )
        );

        ensureVendorProfile(user);

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

    private void ensureVendorProfile(User user) {
        if (user.getRole() != Role.VENDOR || vendorProfileRepository.findByOwnerId(user.getId()).isPresent()) {
            return;
        }

        VendorProfile vendorProfile = VendorProfile.builder()
                .owner(user)
                .businessName((user.getFirstName() + " " + user.getLastName()).trim())
                .category("Wedding Services")
                .categoryTags(new java.util.ArrayList<>())
                .imageUrls(new java.util.ArrayList<>())
                .videoUrls(new java.util.ArrayList<>())
                .rating(0.0)
                .verified(false)
                .build();

        vendorProfileRepository.saveAndFlush(vendorProfile);
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

        User user = userRepository.findByEmailIgnoreCase(request.getEmail().trim())
            .orElseThrow(() -> new RuntimeException("User not found with this email"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}