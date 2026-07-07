package com.eventplatform.repository;

import com.eventplatform.entity.Booking;
import com.eventplatform.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser_Id(Long userId);

    List<Booking> findByVendor_Id(Long vendorId);

    List<Booking> findByVendor_IdAndDate(Long vendorId, LocalDate date);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByUser_IdAndStatus(Long userId, BookingStatus status);
}

