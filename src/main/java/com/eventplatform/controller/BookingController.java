package com.eventplatform.controller;
import com.eventplatform.dto.booking.BookingRequestDto;
import com.eventplatform.dto.booking.BookingResponseDto;
import com.eventplatform.entity.User;
import com.eventplatform.repository.UserRepository;
import com.eventplatform.service.booking.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepo;   // ✅ inject this

    // CREATE BOOKING
    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(
            @Valid @RequestBody BookingRequestDto request,
            @AuthenticationPrincipal UserDetails user) {

        BookingResponseDto response =
                bookingService.createBooking(request, user.getUsername());

        return ResponseEntity.ok(response);
    }


    // GET BOOKING BY ID
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBooking(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                bookingService.getBooking(bookingId)
        );
    }

    // GET BOOKINGS BY STATUS (for logged-in user)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookingResponseDto>> getBookingsByStatus(
            @PathVariable String status,
            @AuthenticationPrincipal UserDetails user) {

        // username = email
        User dbUser = userRepo.findByEmail(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = dbUser.getId();   // ✅ real userId

        return ResponseEntity.ok(
                bookingService.getBookingsByStatus(userId, status)
        );
    }


    // UPDATE BOOKING STATUS (PUT)
    @PutMapping("/{bookingId}/status")
    public ResponseEntity<BookingResponseDto> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam String status) {

        return ResponseEntity.ok(
                bookingService.updateBookingStatus(bookingId, status)
        );
    }


    // MARK BOOKING AS COMPLETED

    @PutMapping("/{bookingId}/complete")
    public ResponseEntity<BookingResponseDto> completeBooking(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                bookingService.completeBooking(bookingId)
        );
    }

    // CANCEL BOOKING (SOFT DELETE)
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long bookingId) {

        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}
