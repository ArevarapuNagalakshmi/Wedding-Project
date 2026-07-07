package com.eventplatform.service.servicepackage;

import com.eventplatform.dto.service.ServicePackageDto;
import com.eventplatform.entity.ServicePackage;
import com.eventplatform.entity.VendorProfile;
import com.eventplatform.repository.AvailabilityRepository;
import com.eventplatform.repository.ServicePackageRepository;
import com.eventplatform.repository.VendorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicePackageServiceImpl implements ServicePackageService {

    private final ServicePackageRepository packageRepo;
    private final VendorProfileRepository vendorRepo;
    private final AvailabilityRepository availabilityRepo;

    // =========================
    // CREATE
    // =========================
    @Override
    @Transactional
    public ServicePackageDto createPackage(Long vendorId, ServicePackageDto dto) {

        VendorProfile vendor = vendorRepo.findById(vendorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vendor not found"));

        if (packageRepo.existsByVendor_IdAndNameIgnoreCase(vendorId, dto.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Package already exists for this vendor");
        }

        ServicePackage entity = mapToEntity(dto, vendor);
        return mapToDto(packageRepo.save(entity));
    }

    // =========================
    // READ
    // =========================
    @Override
    @Transactional(readOnly = true)
    public ServicePackageDto getPackage(Long id) {

        return packageRepo.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Package not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicePackageDto> getPackagesByVendor(Long vendorId) {

        return packageRepo.findByVendor_Id(vendorId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicePackageDto> getPackagesByVendorAndPriceRange(
            Long vendorId,
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        return packageRepo
                .findByVendor_IdAndPriceBetween(vendorId, minPrice, maxPrice)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicePackageDto> searchPackagesByName(String name) {
        return searchPackages(name, null, null, null, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicePackageDto> searchPackages(
            String name,
            String city,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Double minRating,
            LocalDate availableDate) {

        String normalizedName = (name == null || name.isBlank()) ? null : name.trim();
        String normalizedCity = (city == null || city.isBlank()) ? null : city.trim();
        String normalizedCategory = (category == null || category.isBlank()) ? null : category.trim();

        List<ServicePackage> packages = normalizedName != null
                ? packageRepo.findByNameContainingIgnoreCase(normalizedName)
                : packageRepo.findAll();

        return packages.stream()
                .filter(pkg -> pkg.getVendor() != null && Boolean.TRUE.equals(pkg.getVendor().getVerified()))
                .filter(pkg -> normalizedCity == null || normalizedCity.equalsIgnoreCase(pkg.getVendor().getCity()))
                .filter(pkg -> normalizedCategory == null || normalizedCategory.equalsIgnoreCase(pkg.getVendor().getCategory()))
                .filter(pkg -> minPrice == null || (pkg.getPrice() != null && pkg.getPrice().compareTo(minPrice) >= 0))
                .filter(pkg -> maxPrice == null || (pkg.getPrice() != null && pkg.getPrice().compareTo(maxPrice) <= 0))
                .filter(pkg -> minRating == null || (pkg.getVendor().getRating() != null && pkg.getVendor().getRating() >= minRating))
                .filter(pkg -> availableDate == null || !availabilityRepo.existsByVendor_IdAndBlockedDate(pkg.getVendor().getId(), availableDate))
                .map(this::mapToDto)
                .toList();
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    @Transactional
    public ServicePackageDto updatePackage(Long packageId, ServicePackageDto dto) {

        ServicePackage existing = packageRepo.findById(packageId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Package not found"));

        existing.setName(dto.getName());
        existing.setPrice(dto.getPrice());
        existing.setDescription(dto.getDescription());
        existing.setImages(dto.getImages());

        return mapToDto(packageRepo.save(existing));
    }

    // =========================
    // DELETE
    // =========================
    @Override
    @Transactional
    public void deletePackage(Long packageId) {

        if (!packageRepo.existsById(packageId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Package not found");
        }

        packageRepo.deleteById(packageId);
    }

    @Override
    @Transactional
    public void deletePackageByVendor(Long packageId, Long vendorId) {

        ServicePackage servicePackage = packageRepo.findById(packageId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Service not found"));

        // Verify vendor ownership
        if (!servicePackage.getVendor().getId().equals(vendorId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have permission to delete this service");
        }

        packageRepo.deleteById(packageId);
    }

    // =========================
    // MAPPERS
    // =========================
    private ServicePackageDto mapToDto(ServicePackage pkg) {

        return ServicePackageDto.builder()
                .id(pkg.getId())
                .vendorId(pkg.getVendor().getId())
                .name(pkg.getName())
                .price(pkg.getPrice())
                .description(pkg.getDescription())
                .images(pkg.getImages())
                .build();
    }

    private ServicePackage mapToEntity(
            ServicePackageDto dto,
            VendorProfile vendor) {

        return ServicePackage.builder()
                .vendor(vendor)
                .name(dto.getName())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .images(dto.getImages())
                .build();
    }
}
