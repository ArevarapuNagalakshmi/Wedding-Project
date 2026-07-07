package com.eventplatform.controller;

import com.eventplatform.dto.Availability.AvailabilityDto;
import com.eventplatform.service.availability.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
@PreAuthorize("hasRole('VENDOR')")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    // BLOCK DATE
    @PostMapping("/vendor/{vendorId}")
    public ResponseEntity<AvailabilityDto> blockDate(
            @PathVariable Long vendorId,
            @RequestBody AvailabilityDto dto) {

        return ResponseEntity.ok(
                availabilityService.blockDate(vendorId, dto)
        );
    }

    // GET ALL BLOCKED DATES
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<AvailabilityDto>> getBlockedDates(
            @PathVariable Long vendorId) {

        return ResponseEntity.ok(
                availabilityService.getBlockedDates(vendorId)
        );
    }

    // GET BLOCKED DATES IN RANGE
    @GetMapping("/vendor/{vendorId}/range")
    public ResponseEntity<List<AvailabilityDto>> getBlockedDatesInRange(
            @PathVariable Long vendorId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(
                availabilityService.getBlockedDatesInRange(
                        vendorId, startDate, endDate)
        );
    }
    // UNBLOCK DATE
    @DeleteMapping("/vendor/{vendorId}")
    public ResponseEntity<Void> unblockDate(
            @PathVariable Long vendorId,
            @RequestParam LocalDate blockedDate) {

        availabilityService.unblockDate(vendorId, blockedDate);
        return ResponseEntity.noContent().build();
    }

}
