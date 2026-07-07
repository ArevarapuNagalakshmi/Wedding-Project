package com.eventplatform.repository;
import com.eventplatform.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    boolean existsByVendor_IdAndBlockedDate(Long vendorId, LocalDate blockedDate);

    List<Availability> findByVendor_Id(Long vendorId);

    List<Availability> findByVendor_IdAndBlockedDateBetween(
            Long vendorId,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<Availability> findByVendor_IdAndBlockedDate(
            Long vendorId,
            LocalDate blockedDate
    );
}
