package com.eventplatform.service.vendor;

import com.eventplatform.dto.vendor.VendorProfileDto;
import com.eventplatform.entity.User;
import com.eventplatform.entity.VendorProfile;
import com.eventplatform.repository.UserRepository;
import com.eventplatform.repository.VendorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorProfileRepository vendorRepo;
    private final UserRepository userRepository;

    // =========================
    // CREATE VENDOR PROFILE
    // =========================
    @Override
    @Transactional
    public VendorProfileDto createVendor(VendorProfileDto dto, String ownerEmail) {

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() ->
                        new RuntimeException("Owner user not found: " + ownerEmail));

        // Prevent duplicate vendor profile per user
        vendorRepo.findByOwnerId(owner.getId()).ifPresent(v -> {
            throw new RuntimeException("Vendor profile already exists for this user");
        });

        VendorProfile vendor = VendorProfile.builder()
                .owner(owner)
                .businessName(dto.getBusinessName())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .city(dto.getCity())
                .address(dto.getAddress())
                .pricingRange(dto.getPricingRange())
                .responseTime(dto.getResponseTime())
                .categoryTags(dto.getCategoryTags() != null ? dto.getCategoryTags() : new ArrayList<>())
                .gstin(dto.getGstin())
                .rating(0.0)
                .verified(false)
                .imageUrls(dto.getImageUrls() != null ? dto.getImageUrls() : new ArrayList<>())
                .videoUrls(dto.getVideoUrls() != null ? dto.getVideoUrls() : new ArrayList<>())
                .build();

        VendorProfile saved = vendorRepo.save(vendor);
        vendorRepo.flush();
        return mapToDto(saved);
    }

    // =========================
    // GET VENDOR BY ID
    // =========================
    @Override
    @Transactional(readOnly = true)
    public VendorProfileDto getVendor(Long id) {

        VendorProfile vendor = vendorRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found with id: " + id));

        return mapToDto(vendor);
    }

    // =========================
    // UPDATE VENDOR PROFILE
    // =========================
    @Override
    @Transactional
    public VendorProfileDto updateProfile(Long id, VendorProfileDto dto) {

        VendorProfile vendor = vendorRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found with id: " + id));

        vendor.setBusinessName(dto.getBusinessName());
        vendor.setCategory(dto.getCategory());
        vendor.setDescription(dto.getDescription());
        vendor.setCity(dto.getCity());
        vendor.setAddress(dto.getAddress());
        vendor.setPricingRange(dto.getPricingRange());
        vendor.setResponseTime(dto.getResponseTime());
        vendor.setCategoryTags(dto.getCategoryTags());
        vendor.setGstin(dto.getGstin());

        if (dto.getImageUrls() != null) {
            vendor.setImageUrls(dto.getImageUrls());
        }
        if (dto.getVideoUrls() != null) {
            vendor.setVideoUrls(dto.getVideoUrls());
        }

        VendorProfile updated = vendorRepo.save(vendor);
        vendorRepo.flush();
        return mapToDto(updated);
    }

    // =========================
    // VERIFY VENDOR (ADMIN)
    // =========================
    @Override
    @Transactional
    public VendorProfileDto verifyVendor(Long id) {

        VendorProfile vendor = vendorRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found with id: " + id));

        vendor.setVerified(true);
        VendorProfile saved = vendorRepo.save(vendor);
        vendorRepo.flush();

        return mapToDto(saved);
    }

    // =========================
    // GET ALL VERIFIED VENDORS
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<VendorProfileDto> getAllVerifiedVendors() {

        return vendorRepo.findByVerifiedTrue()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // =========================
    // GET VENDORS BY OWNER
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<VendorProfileDto> getVendorsByOwner(String ownerEmail) {

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() ->
                        new RuntimeException("Owner user not found: " + ownerEmail));

        return vendorRepo.findByOwnerId(owner.getId())
                .map(v -> List.of(mapToDto(v)))
                .orElseGet(List::of);
    }

    // =========================
    // DEACTIVATE VENDOR (SOFT DELETE)
    // =========================
    @Override
    @Transactional
    public void deactivateVendor(Long id) {

        VendorProfile vendor = vendorRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found with id: " + id));

        vendor.setVerified(false); // or vendor.setActive(false) if you add active flag
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
                .gstin(v.getGstin())
                .rating(v.getRating())
                .verified(v.getVerified())
                .imageUrls(v.getImageUrls())
                .videoUrls(v.getVideoUrls())
                .build();
    }
}
