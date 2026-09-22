package com.eventplatform.controller;
import com.eventplatform.dto.service.ServicePackageDto;
import com.eventplatform.entity.User;
import com.eventplatform.entity.VendorProfile;
import com.eventplatform.repository.UserRepository;
import com.eventplatform.repository.VendorProfileRepository;
import com.eventplatform.service.servicepackage.ServicePackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class ServicePackageController {

    private final ServicePackageService packageService;
    private final VendorProfileRepository vendorProfileRepo;
    private final UserRepository userRepository;

    // CREATE
        @PostMapping("/vendor/{vendorId:[0-9]+}")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ServicePackageDto> create(
            @PathVariable Long vendorId,
            @RequestBody ServicePackageDto dto) {

        return ResponseEntity.ok(
                packageService.createPackage(vendorId, dto)
        );
    }

    // READ
    @GetMapping("/{id}")
    public ResponseEntity<ServicePackageDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(packageService.getPackage(id));
    }

        @GetMapping("/vendor/{vendorId:[0-9]+}")
    public ResponseEntity<List<ServicePackageDto>> getByVendor(
            @PathVariable Long vendorId) {

        return ResponseEntity.ok(
                packageService.getPackagesByVendor(vendorId)
        );
    }

    // Convenience endpoint for current authenticated vendor to fetch their packages
    @GetMapping("/vendor/my")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<List<ServicePackageDto>> getMyPackages(Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        VendorProfile vendorProfile = vendorProfileRepo.findByOwnerId(user.getId())
                .orElseThrow(() -> new RuntimeException("Vendor profile not found"));

        return ResponseEntity.ok(packageService.getPackagesByVendor(vendorProfile.getId()));
    }

        @GetMapping("/vendor/{vendorId:[0-9]+}/price-range")
    public ResponseEntity<List<ServicePackageDto>> byPrice(
            @PathVariable Long vendorId,
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {

        return ResponseEntity.ok(
                packageService.getPackagesByVendorAndPriceRange(
                        vendorId, minPrice, maxPrice)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<ServicePackageDto>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, name = "q") String q,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) java.time.LocalDate availableDate) {

        // Accept `q` as an alias for `name` to support frontend clients that send `q`
        final String resolvedName = (q != null && !q.isBlank()) ? q : name;

        return ResponseEntity.ok(
                packageService.searchPackages(
                        resolvedName, city, category,
                        minPrice, maxPrice, minRating, availableDate)
        );
    }

    // Convenience endpoint to return all packages (used by legacy frontend getAllServices)
    @GetMapping("/services")
    public ResponseEntity<List<ServicePackageDto>> getAll() {
        return ResponseEntity.ok(packageService.searchPackages(null, null, null, null, null, null, null));
    }

    // UPDATE
    @PutMapping("/{packageId}")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ServicePackageDto> update(
            @PathVariable Long packageId,
            @RequestBody ServicePackageDto dto) {

        return ResponseEntity.ok(
                packageService.updatePackage(packageId, dto)
        );
    }

    // DELETE
    @DeleteMapping("/{packageId}")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long packageId) {

        // Get current user from authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Check if user is admin - if so, allow deletion without vendor check
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            packageService.deletePackage(packageId);
        } else {
            // For vendors, validate ownership
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            VendorProfile vendorProfile = vendorProfileRepo.findByOwnerId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Vendor profile not found"));

            packageService.deletePackageByVendor(packageId, vendorProfile.getId());
        }

        return ResponseEntity.noContent().build();
    }
}
