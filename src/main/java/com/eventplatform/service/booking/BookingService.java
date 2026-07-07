package com.eventplatform.service.booking;

import com.eventplatform.dto.booking.BookingRequestDto;
import com.eventplatform.dto.booking.BookingResponseDto;

import java.util.List;

public interface BookingService {


    // CREATE
    BookingResponseDto createBooking(
            BookingRequestDto request,
            String requesterEmail
    );


    // READ
    BookingResponseDto getBooking(Long bookingId);

    List<BookingResponseDto> getBookingsByStatus(Long userId, String status);

    // UPDATE

    BookingResponseDto updateBookingStatus(Long bookingId, String status);

    BookingResponseDto completeBooking(Long bookingId);


    // DELETE

    void cancelBooking(Long bookingId);
}
