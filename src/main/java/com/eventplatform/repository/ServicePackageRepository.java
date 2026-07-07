package com.eventplatform.repository;

import com.eventplatform.entity.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {

    List<ServicePackage> findByVendor_Id(Long vendorId);

    List<ServicePackage> findByVendor_IdAndPriceBetween(
            Long vendorId,
            BigDecimal min,
            BigDecimal max
    );

    List<ServicePackage> findByNameContainingIgnoreCase(String name);

    boolean existsByVendor_IdAndNameIgnoreCase(Long vendorId, String name);
}
