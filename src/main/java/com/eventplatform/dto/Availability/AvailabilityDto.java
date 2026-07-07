package com.eventplatform.dto.Availability;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AvailabilityDto {

    private Long id;
    private Long vendorId;
    private LocalDate blockedDate;
    private String reason;
}

