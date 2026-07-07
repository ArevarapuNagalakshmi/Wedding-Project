package com.eventplatform.controller;
import com.eventplatform.dto.vendor.VendorProfileDto;
import com.eventplatform.service.admin.AdminService;
import com.eventplatform.service.admin.AdminServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // 🔐 Admin-only access
public class AdminController {

    private final AdminServiceImpl adminService;


    // GET – All pending vendor profiles
    @GetMapping("/vendors/pending")
    public ResponseEntity<List<VendorProfileDto>> getPendingVendors() {

        return ResponseEntity.ok(
                adminService.getPendingVendors()
        );
    }


    // PUT – Approve / verify a vendor
    @PutMapping("/vendors/{vendorId}/approve")
    public ResponseEntity<VendorProfileDto> approveVendor(
            @PathVariable Long vendorId) {

        VendorProfileDto approved =
                adminService.approveVendor(vendorId);

        return ResponseEntity.ok(approved);
    }


    // DELETE – Reject a vendor (unverified only)
    @DeleteMapping("/vendors/{vendorId}/reject")
    public ResponseEntity<Map<String, String>> rejectVendor(
            @PathVariable Long vendorId) {

        adminService.rejectVendor(vendorId);

        return ResponseEntity.ok(
                Map.of("status", "rejected")
        );
    }


    // PUT – Deactivate a vendor (soft delete)
    @PutMapping("/vendors/{vendorId}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateVendor(
            @PathVariable Long vendorId) {

        adminService.deactivateVendor(vendorId);

        return ResponseEntity.ok(
                Map.of("status", "deactivated")
        );
    }
}
