package com.eventplatform.controller;

import com.eventplatform.dto.payment.PaymentInitiateDto;
import com.eventplatform.dto.payment.PaymentResponseDto;
import com.eventplatform.entity.PaymentStatus;
import com.eventplatform.entity.PaymentTransaction;
import com.eventplatform.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // =========================
    // POST – Initiate payment
    // =========================
    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDto> initiatePayment(
            @Valid @RequestBody PaymentInitiateDto dto) {

        return ResponseEntity.ok(paymentService.initiatePayment(dto));
    }

    // =========================
    // GET – By bookingId
    // =========================
    @GetMapping("/by-booking/{bookingId}")
    public ResponseEntity<PaymentResponseDto> getByBookingId(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                paymentService.getPaymentByBookingId(bookingId)
        );
    }

    // =========================
    // GET – By gateway reference
    // =========================
    @GetMapping("/by-gateway/{gatewayReference}")
    public ResponseEntity<PaymentResponseDto> getByGatewayReference(
            @PathVariable String gatewayReference) {

        return ResponseEntity.ok(
                paymentService.getPaymentByGatewayReference(gatewayReference)
        );
    }

    // =========================
    // GET – By status
    // =========================
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponseDto>> getByStatus(
            @PathVariable PaymentStatus status) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByStatus(status)
        );
    }

    // =========================
    // PUT – Update payment status
    // =========================
    @PutMapping("/{paymentId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponseDto> updateStatus(
            @PathVariable Long paymentId,
            @RequestParam PaymentStatus status) {

        return ResponseEntity.ok(
                paymentService.updatePaymentStatus(paymentId, status)
        );
    }

    // =========================
    // DELETE – Delete payment
    // =========================
    @DeleteMapping("/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePayment(
            @PathVariable Long paymentId) {

        paymentService.deletePayment(paymentId);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // Webhook endpoint
    // =========================
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Signature", required = false) String signature) {

        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok("OK");
    }
}
