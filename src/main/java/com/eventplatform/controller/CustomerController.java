package com.eventplatform.controller;

import com.eventplatform.dto.customer.CustomerProfileDto;
import com.eventplatform.service.customer.CustomerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerProfileService customerProfileService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerProfileDto> getCustomerProfile(
            @AuthenticationPrincipal UserDetails authenticatedUser
    ) {
        CustomerProfileDto profile = customerProfileService.getProfile(authenticatedUser.getUsername());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerProfileDto> updateCustomerProfile(
            @AuthenticationPrincipal UserDetails authenticatedUser,
            @RequestBody CustomerProfileDto customerProfileDto
    ) {
        CustomerProfileDto profile = customerProfileService.updateProfile(authenticatedUser.getUsername(), customerProfileDto);
        return ResponseEntity.ok(profile);
    }
}
