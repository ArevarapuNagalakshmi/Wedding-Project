package com.eventplatform.repository;

import com.eventplatform.entity.VendorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VendorProfileRepository extends JpaRepository<VendorProfile, Long> {

    // ✅ FIXED: nested property (owner.id)
    Optional<VendorProfile> findByOwnerId(Long ownerId);

    // Search filters
    List<VendorProfile> findByCityIgnoreCase(String city);

    List<VendorProfile> findByCategoryIgnoreCase(String category);

    List<VendorProfile> findByCityIgnoreCaseAndCategoryIgnoreCase(
            String city,
            String category
    );

    // Verification filters
    List<VendorProfile> findByVerifiedTrue();

    List<VendorProfile> findByVerifiedFalse();
}
