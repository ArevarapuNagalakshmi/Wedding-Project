package com.eventplatform.service.payment;

import com.eventplatform.dto.payment.PaymentInitiateDto;
import com.eventplatform.dto.payment.PaymentResponseDto;
import com.eventplatform.entity.*;
import com.eventplatform.repository.BookingRepository;
import com.eventplatform.repository.PaymentTransactionRepository;
import com.eventplatform.util.CommissionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentTransactionRepository paymentRepo;
    private final BookingRepository bookingRepo;

    // =========================
    // POST – Initiate payment
    // =========================
    @Override
    @Transactional
    public PaymentResponseDto initiatePayment(PaymentInitiateDto dto) {

        Booking booking = bookingRepo.findById(dto.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        paymentRepo.findByBookingId(booking.getId())
                .ifPresent(tx -> {
                    throw new RuntimeException("Payment already initiated for this booking");
                });

        BigDecimal commissionPercent = new BigDecimal("10");
        BigDecimal commission =
                CommissionUtil.platformFee(dto.getAmount(), commissionPercent);

        BigDecimal vendorAmount = dto.getAmount().subtract(commission);

        PaymentTransaction tx = PaymentTransaction.builder()
                .booking(booking)
                .amount(dto.getAmount())
                .commissionAmount(commission)
                .vendorAmount(vendorAmount)
                .status(PaymentStatus.INITIATED)
                .timestamp(Instant.now())
                .gatewayReference("SIMULATED_" + System.currentTimeMillis())
                .build();

        PaymentTransaction saved = paymentRepo.save(tx);

        return mapToDto(saved);
    }

    // =========================
    // GET – by bookingId
    // =========================
    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentByBookingId(Long bookingId) {

        PaymentTransaction tx = paymentRepo.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException(
                        "Payment not found for bookingId: " + bookingId));

        return mapToDto(tx);
    }

    // =========================
    // GET – by gateway reference
    // =========================
    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentByGatewayReference(String gatewayReference) {

        PaymentTransaction tx = paymentRepo.findByGatewayReference(gatewayReference)
                .orElseThrow(() -> new RuntimeException(
                        "Payment not found for gatewayReference: " + gatewayReference));

        return mapToDto(tx);
    }

    // =========================
    // GET – by status
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByStatus(PaymentStatus status) {

        return paymentRepo.findByStatus(status)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    // =========================
    // PUT – update payment status
    // =========================
    @Override
    @Transactional
    public PaymentResponseDto updatePaymentStatus(Long id, PaymentStatus status) {

        PaymentTransaction tx = paymentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        tx.setStatus(status);

        if (status == PaymentStatus.SUCCESS) {
            tx.getBooking().setStatus(BookingStatus.CONFIRMED);
        } else if (status == PaymentStatus.FAILED || status == PaymentStatus.REFUNDED) {
            tx.getBooking().setStatus(BookingStatus.CANCELLED);
        }

        bookingRepo.save(tx.getBooking());
        PaymentTransaction saved = paymentRepo.save(tx);

        return mapToDto(saved);
    }

    // =========================
    // DELETE – delete payment
    // =========================
    @Override
    @Transactional
    public void deletePayment(Long id) {

        PaymentTransaction tx = paymentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        paymentRepo.delete(tx);
    }

    // =========================
    // Webhook – simulate gateway callback
    // =========================
    @Override
    @Transactional
    public void handleWebhook(String payload, String signature) {

        String[] parts = payload.split(":");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid webhook payload");
        }

        String gatewayRef = parts[0];
        String statusStr = parts[1];

        PaymentTransaction tx = paymentRepo
                .findByGatewayReference(gatewayRef)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        Booking booking = tx.getBooking();

        PaymentStatus newStatus;
        try {
            newStatus = PaymentStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid payment status from gateway: " + statusStr);
        }

        tx.setStatus(newStatus);

        if (newStatus == PaymentStatus.SUCCESS) {
            booking.setStatus(BookingStatus.CONFIRMED);
        } else if (newStatus == PaymentStatus.FAILED || newStatus == PaymentStatus.REFUNDED) {
            booking.setStatus(BookingStatus.CANCELLED);
        }

        paymentRepo.save(tx);
        bookingRepo.save(booking);
    }

    // =========================
    // Mapper
    // =========================
    private PaymentResponseDto mapToDto(PaymentTransaction tx) {

        return PaymentResponseDto.builder()
                .id(tx.getId())
                .bookingId(tx.getBooking().getId())
                .amount(tx.getAmount())
                .commissionAmount(tx.getCommissionAmount())
                .vendorAmount(tx.getVendorAmount())
                .status(tx.getStatus().name())
                .gatewayReference(tx.getGatewayReference())
                .timestamp(tx.getTimestamp())
                .build();
    }
}
