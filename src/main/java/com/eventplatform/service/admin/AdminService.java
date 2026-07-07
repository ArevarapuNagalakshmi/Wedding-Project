package com.eventplatform.service.admin;
import java.util.List;
import com.eventplatform.dto.vendor.VendorProfileDto;

public interface AdminService {

    // Get all vendors waiting for admin approval
    List<VendorProfileDto> getPendingVendors();

    // Approve / verify a vendor
    VendorProfileDto approveVendor(Long vendorId);

    // Optional: reject or deactivate vendor
     void rejectVendor(Long vendorId);
    void deactivateVendor(Long vendorId);
}
