package com.eventplatform.controller;

import com.eventplatform.dto.booking.BookingResponseDto;
import com.eventplatform.entity.User;
import com.eventplatform.repository.UserRepository;
import com.eventplatform.repository.VendorProfileRepository;
import com.eventplatform.service.booking.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vendor/bookings")
@RequiredArgsConstructor
public class VendorBookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final VendorProfileRepository vendorProfileRepository;

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getVendorBookings(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long vendorId = vendorProfileRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new RuntimeException("Vendor profile not found"))
                .getId();

        return ResponseEntity.ok(bookingService.getBookingsForVendor(vendorId));
    }
}