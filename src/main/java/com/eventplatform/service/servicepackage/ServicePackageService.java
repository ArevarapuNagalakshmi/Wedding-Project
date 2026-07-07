package com.eventplatform.service.servicepackage;


import com.eventplatform.dto.service.ServicePackageDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ServicePackageService {

    ServicePackageDto createPackage(Long vendorId, ServicePackageDto dto);

    ServicePackageDto getPackage(Long id);

    List<ServicePackageDto> getPackagesByVendor(Long vendorId);

    List<ServicePackageDto> getPackagesByVendorAndPriceRange(
            Long vendorId,
            BigDecimal minPrice,
            BigDecimal maxPrice
    );

    List<ServicePackageDto> searchPackagesByName(String name);

    List<ServicePackageDto> searchPackages(
            String name,
            String city,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Double minRating,
            LocalDate availableDate
    );

    ServicePackageDto updatePackage(Long packageId, ServicePackageDto dto);

    void deletePackage(Long packageId);

    void deletePackageByVendor(Long packageId, Long vendorId);
}
