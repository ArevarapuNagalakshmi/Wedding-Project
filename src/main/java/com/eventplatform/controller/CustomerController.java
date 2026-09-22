package com.eventplatform.controller;

import com.eventplatform.dto.customer.CustomerProfileDto;
import com.eventplatform.service.customer.CustomerProfileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerProfileService customerProfileService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerProfileDto> getCustomerProfile(
            @AuthenticationPrincipal UserDetails authenticatedUser
    ) {
        log.info("📥 GET /api/customers/profile - User: {}", authenticatedUser.getUsername());
        
        try {
            CustomerProfileDto profile = customerProfileService.getProfile(authenticatedUser.getUsername());
            log.info("✅ Profile loaded for user: {}", authenticatedUser.getUsername());
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("❌ Error loading profile for user: {}", authenticatedUser.getUsername(), e);
            throw e;
        }
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerProfileDto> updateCustomerProfile(
            @AuthenticationPrincipal UserDetails authenticatedUser,
            @RequestBody CustomerProfileDto customerProfileDto
    ) {
        log.info("📤 PUT /api/customers/profile - User: {}", authenticatedUser.getUsername());
        
        try {
            CustomerProfileDto profile = customerProfileService.updateProfile(authenticatedUser.getUsername(), customerProfileDto);
            log.info("✅ Profile updated for user: {}", authenticatedUser.getUsername());
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("❌ Error updating profile for user: {}", authenticatedUser.getUsername(), e);
            throw e;
        }
    }
}
