package com.eventplatform.service.booking;

import com.eventplatform.dto.booking.BookingRequestDto;
import com.eventplatform.dto.booking.BookingResponseDto;
import com.eventplatform.entity.*;
import com.eventplatform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final VendorProfileRepository vendorRepo;
    private final UserRepository userRepo;
    private final AvailabilityRepository availabilityRepository;

    // CREATE
    @Override
    @Transactional
    public BookingResponseDto createBooking(
            BookingRequestDto req,
            String requesterEmail
    ) {
        User user = userRepo.findByEmail(requesterEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        VendorProfile vendor = vendorRepo.findById(req.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        boolean blocked = availabilityRepository
                .existsByVendor_IdAndBlockedDate(vendor.getId(), req.getDate());

        if (blocked) {
            throw new RuntimeException("Vendor not available on selected date");
        }

        boolean alreadyBooked =
                !bookingRepository
                        .findByVendor_IdAndDate(vendor.getId(), req.getDate())
                        .isEmpty();

        if (alreadyBooked) {
            throw new RuntimeException("Date already booked");
        }

        Booking booking = Booking.builder()
                .user(user)
                .vendor(vendor)
                .date(req.getDate())
                .totalAmount(req.getAmount())
                .status(BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        
        // Ensure the ID is generated and persisted
        bookingRepository.flush();

        return mapToDto(savedBooking);
    }

    // READ
    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        return mapToDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsByStatus(Long userId, String status) {

        BookingStatus bookingStatus;
        try {
            bookingStatus = BookingStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid booking status: " + status);
        }

        List<Booking> bookings =
                bookingRepository.findByUser_IdAndStatus(userId, bookingStatus);

        return bookings.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsForVendor(Long vendorId) {
        return bookingRepository.findByVendor_Id(vendorId).stream()
                .map(this::mapToDto)
                .toList();
    }

    // UPDATE

    @Override
    @Transactional
    public BookingResponseDto updateBookingStatus(Long bookingId, String status) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        BookingStatus newStatus;
        try {
            newStatus = BookingStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid booking status: " + status);
        }

        booking.setStatus(newStatus);
        bookingRepository.save(booking);

        return mapToDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto completeBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Only CONFIRMED bookings can be completed");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        return mapToDto(booking);
    }

    // DELETE (SOFT CANCEL)
    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new RuntimeException("Completed bookings cannot be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    // COMMON ENTITY → DTO MAPPER
    private BookingResponseDto mapToDto(Booking booking) {

        return BookingResponseDto.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .vendorId(booking.getVendor().getId())
                .date(booking.getDate())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus().name())
                .build();
    }
}
