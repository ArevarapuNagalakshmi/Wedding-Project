package com.eventplatform.service.availability;


import com.eventplatform.dto.Availability.AvailabilityDto;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {

    // BLOCK date
    AvailabilityDto blockDate(Long vendorId, AvailabilityDto dto);


    // GET all blocked dates
    List<AvailabilityDto> getBlockedDates(Long vendorId);

    // GET blocked dates in range
    List<AvailabilityDto> getBlockedDatesInRange(
            Long vendorId,
            LocalDate startDate,
            LocalDate endDate
    );
    // UNBLOCK date
    void unblockDate(Long vendorId, LocalDate blockedDate);
}
