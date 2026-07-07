package com.eventplatform.controller;

import com.eventplatform.dto.vendor.VendorProfileDto;
import com.eventplatform.service.vendor.VendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    // POST APIs (CREATE)
    //  Create vendor profile (VENDOR only)
    @PostMapping
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<VendorProfileDto> createVendor(
            @Valid @RequestBody VendorProfileDto dto,
            @AuthenticationPrincipal UserDetails user) {

        return ResponseEntity.ok(
                vendorService.createVendor(dto, user.getUsername())
        );
    }
    // GET APIs (READ)
    // Get current logged-in vendor profile
    @GetMapping("/profile")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<VendorProfileDto> getCurrentVendorProfile(
            @AuthenticationPrincipal UserDetails user) {
        System.out.println("Authenticated User: " + user);

        List<VendorProfileDto> vendors = vendorService.getVendorsByOwner(user.getUsername());
        VendorProfileDto profile = vendors.stream().findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "Vendor profile not found. Please create a vendor profile first by posting to /api/vendors"
                ));

        return ResponseEntity.ok(profile);
    }

    // Get vendor profile by numeric ID (public)
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<VendorProfileDto> getVendor(@PathVariable Long id) {
        return ResponseEntity.ok(vendorService.getVendor(id));
    }

    // Get all verified vendors (public / search fallback)
    @GetMapping("/verified")
    public ResponseEntity<List<VendorProfileDto>> getAllVerifiedVendors() {

        return ResponseEntity.ok(
                vendorService.getAllVerifiedVendors()
        );
    }

    //Get vendors owned by logged-in vendor (Vendor dashboard)
    @GetMapping("/my")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<List<VendorProfileDto>> getMyVendors(
            @AuthenticationPrincipal UserDetails user) {

        return ResponseEntity.ok(
                vendorService.getVendorsByOwner(user.getUsername())
        );
    }

    // PUT APIs (UPDATE)

    //Update current logged-in vendor profile (VENDOR only)
    @PutMapping("/profile")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<VendorProfileDto> updateCurrentVendorProfile(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody VendorProfileDto dto) {

        List<VendorProfileDto> vendors = vendorService.getVendorsByOwner(user.getUsername());
        VendorProfileDto existing = vendors.stream().findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "Vendor profile not found. Please create a vendor profile first by posting to /api/vendors"
                ));

        return ResponseEntity.ok(
                vendorService.updateProfile(existing.getId(), dto)
        );
    }

    @PostMapping("/profile/upload")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<List<String>> uploadPortfolioFiles(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam("type") String type,
            @RequestParam("files") MultipartFile[] files) throws IOException {

        List<VendorProfileDto> vendors = vendorService.getVendorsByOwner(user.getUsername());
        VendorProfileDto existing = vendors.stream().findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "Vendor profile not found. Please create a vendor profile first by posting to /api/vendors"
                ));

        if (files == null || files.length == 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No files uploaded"
            );
        }
        if (!List.of("images", "videos").contains(type)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Upload type must be 'images' or 'videos'"
            );
        }

        Path uploadRoot = Paths.get(uploadDir, "vendor-" + existing.getId(), type);
        Files.createDirectories(uploadRoot);

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String storedFilename = UUID.randomUUID() + "-" + originalFilename;
            Path targetLocation = uploadRoot.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            urls.add("/uploads/vendor-" + existing.getId() + "/" + type + "/" + storedFilename);
        }

        return ResponseEntity.ok(urls);
    }

    //Update vendor profile (VENDOR or ADMIN)
    @PutMapping("/{vendorId}")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<VendorProfileDto> updateVendor(
            @PathVariable Long vendorId,
            @Valid @RequestBody VendorProfileDto dto) {

        return ResponseEntity.ok(
                vendorService.updateProfile(vendorId, dto)
        );
    }

    //  Verify vendor (ADMIN only)
    @PutMapping("/{vendorId}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VendorProfileDto> verifyVendor(
            @PathVariable Long vendorId) {

        return ResponseEntity.ok(
                vendorService.verifyVendor(vendorId)
        );
    }

    // DELETE APIs (SOFT DELETE)

    //  Deactivate vendor (soft delete) (ADMIN only)
    @DeleteMapping("/{vendorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateVendor(
            @PathVariable Long vendorId) {

        vendorService.deactivateVendor(vendorId);
        return ResponseEntity.noContent().build();
    }
}
