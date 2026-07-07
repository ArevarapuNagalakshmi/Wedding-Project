package com.eventplatform.dto.payment;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {

    private Long id;
    private Long bookingId;
    private BigDecimal amount;
    private BigDecimal commissionAmount;
    private BigDecimal vendorAmount;
    private String status;
    private String gatewayReference;
    private Instant timestamp;
}
