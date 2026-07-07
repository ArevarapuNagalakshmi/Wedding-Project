package com.eventplatform.service.payment;

import com.eventplatform.dto.payment.PaymentInitiateDto;
import com.eventplatform.dto.payment.PaymentResponseDto;
import com.eventplatform.entity.PaymentStatus;

import java.util.List;

public interface PaymentService {

    // POST
    PaymentResponseDto initiatePayment(PaymentInitiateDto dto);

    // GET
    PaymentResponseDto getPaymentByBookingId(Long bookingId);

    PaymentResponseDto getPaymentByGatewayReference(String gatewayReference);

    List<PaymentResponseDto> getPaymentsByStatus(PaymentStatus status);

    // PUT
    PaymentResponseDto updatePaymentStatus(Long id, PaymentStatus status);

    // DELETE
    void deletePayment(Long id);

    // Webhook
    void handleWebhook(String payload, String signature);
}

