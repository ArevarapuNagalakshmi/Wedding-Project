package com.eventplatform.service.vendor;


import com.eventplatform.dto.vendor.VendorProfileDto;

import java.util.List;

public interface VendorService {

    // ✅ Create vendor profile (by logged-in vendor)
    VendorProfileDto createVendor(VendorProfileDto dto, String ownerEmail);

    //  Get vendor by ID (public / search / details page)
    VendorProfileDto getVendor(Long id);

    //  Update vendor profile (by owner)
    VendorProfileDto updateProfile(Long id, VendorProfileDto dto);

    // 🟡 Optional but recommended (Admin / future use)

    // Verify vendor (ADMIN)
    VendorProfileDto verifyVendor(Long id);

    // Get all verified vendors (Search fallback)
    List<VendorProfileDto> getAllVerifiedVendors();

    // Get vendors owned by a user (Vendor dashboard)
    List<VendorProfileDto> getVendorsByOwner(String ownerEmail);

    // Soft delete / deactivate vendor
    void deactivateVendor(Long id);
}
