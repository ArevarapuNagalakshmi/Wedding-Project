package com.eventplatform.service.availability;

import com.eventplatform.dto.Availability.AvailabilityDto;
import com.eventplatform.entity.Availability;
import com.eventplatform.entity.VendorProfile;
import com.eventplatform.repository.AvailabilityRepository;
import com.eventplatform.repository.VendorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepo;
    private final VendorProfileRepository vendorRepo;

    // =========================
    // BLOCK DATE
    // =========================
    @Override
    @Transactional
    public AvailabilityDto blockDate(Long vendorId, AvailabilityDto dto) {

        VendorProfile vendor = vendorRepo.findById(vendorId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Vendor not found"));

        if (availabilityRepo.existsByVendor_IdAndBlockedDate(
                vendorId, dto.getBlockedDate())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Vendor already blocked on this date");
        }

        Availability availability = Availability.builder()
                .vendor(vendor)
                .blockedDate(dto.getBlockedDate())
                .reason(dto.getReason())
                .build();

        return mapToDto(availabilityRepo.save(availability));
    }


    // =========================
    // GET ALL BLOCKED DATES
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityDto> getBlockedDates(Long vendorId) {

        return availabilityRepo.findByVendor_Id(vendorId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    // =========================
    // GET BLOCKED DATES IN RANGE
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityDto> getBlockedDatesInRange(
            Long vendorId,
            LocalDate startDate,
            LocalDate endDate) {

        return availabilityRepo
                .findByVendor_IdAndBlockedDateBetween(
                        vendorId, startDate, endDate)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    // =========================
    // MAPPER (VERY IMPORTANT)
    // =========================
    private AvailabilityDto mapToDto(Availability a) {

        return AvailabilityDto.builder()
                .id(a.getId())
                .vendorId(a.getVendor().getId())
                .blockedDate(a.getBlockedDate())
                .reason(a.getReason())
                .build();
    }
    // =========================
    // UNBLOCK DATE
    // =========================
    @Override
    @Transactional
    public void unblockDate(Long vendorId, LocalDate blockedDate) {

        Availability availability = availabilityRepo
                .findByVendor_IdAndBlockedDate(vendorId, blockedDate)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Blocked date not found"));

        availabilityRepo.delete(availability);
    }
}



