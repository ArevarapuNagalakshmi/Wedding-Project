package com.eventplatform.dto.booking;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponseDto {
    private Long id;
    private Long userId;
    private Long vendorId;
    private LocalDate date;
    private BigDecimal totalAmount;
    private String status;
}
