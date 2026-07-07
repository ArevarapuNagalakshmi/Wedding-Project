package com.eventplatform.service.admin;

import com.eventplatform.dto.vendor.VendorProfileDto;
import com.eventplatform.entity.VendorProfile;
import com.eventplatform.repository.VendorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final VendorProfileRepository vendorRepo;

    // =========================
    // GET ALL PENDING VENDORS
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<VendorProfileDto> getPendingVendors() {

        return vendorRepo.findByVerifiedFalse()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // =========================
    // APPROVE / VERIFY VENDOR
    // =========================
    @Override
    @Transactional
    public VendorProfileDto approveVendor(Long vendorId) {

        VendorProfile vendor = vendorRepo.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (Boolean.TRUE.equals(vendor.getVerified())) {   // ✅ FIX
            throw new RuntimeException("Vendor is already verified");
        }

        vendor.setVerified(true);
        VendorProfile saved = vendorRepo.save(vendor);

        return mapToDto(saved);
    }

    // =========================
    // REJECT VENDOR (ADMIN)
    // =========================
    @Override
    @Transactional
    public void rejectVendor(Long vendorId) {

        VendorProfile vendor = vendorRepo.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (Boolean.TRUE.equals(vendor.getVerified())) {   // ✅ FIX
            throw new RuntimeException("Verified vendors cannot be rejected");
        }

        vendorRepo.delete(vendor);
    }

    // =========================
    // DEACTIVATE VENDOR (SOFT DELETE)
    // =========================
    @Override
    @Transactional
    public void deactivateVendor(Long vendorId) {

        VendorProfile vendor = vendorRepo.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (!Boolean.TRUE.equals(vendor.getVerified())) {  // ✅ FIX
            throw new RuntimeException("Only verified vendors can be deactivated");
        }

        vendor.setVerified(false);
        vendorRepo.save(vendor);
    }

    // =========================
    // ENTITY → DTO MAPPER
    // =========================
    private VendorProfileDto mapToDto(VendorProfile v) {

        return VendorProfileDto.builder()
                .id(v.getId())
                .businessName(v.getBusinessName())
                .category(v.getCategory())
                .description(v.getDescription())
                .city(v.getCity())
                .address(v.getAddress())
                .pricingRange(v.getPricingRange())
                .responseTime(v.getResponseTime())
                .categoryTags(v.getCategoryTags())
                .imageUrls(v.getImageUrls())
                .videoUrls(v.getVideoUrls())
                .verified(v.getVerified())   // ✅ keep consistent with DTO
                .build();
    }
}
