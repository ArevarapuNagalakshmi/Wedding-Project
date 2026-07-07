package com.eventplatform.service.search;

import com.eventplatform.dto.vendor.VendorProfileDto;
import com.eventplatform.entity.VendorProfile;
import com.eventplatform.repository.VendorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final VendorProfileRepository vendorRepo;


    // SIMPLE SEARCH (MVP)
    @Override
    public List<VendorProfileDto> search(String city, String category) {
        return search(city, category, 0, 50, "businessName");
    }

    // ADVANCED SEARCH
    @Override
    public List<VendorProfileDto> search(
            String city,
            String category,
            int page,
            int size,
            String sortBy) {

        // Normalize inputs
        if (city != null) city = city.trim();
        if (category != null) category = category.trim();

        if (city != null && city.isBlank()) city = null;
        if (category != null && category.isBlank()) category = null;

        List<VendorProfile> vendors;

        // City + Category
        if (city != null && category != null) {
            vendors = vendorRepo
                    .findByCityIgnoreCaseAndCategoryIgnoreCase(city, category);

            // City only
        } else if (city != null) {
            vendors = vendorRepo.findByCityIgnoreCase(city);

            // Category only
        } else if (category != null) {
            vendors = vendorRepo.findByCategoryIgnoreCase(category);

            // No filters → all verified vendors
        } else {
            vendors = vendorRepo.findByVerifiedTrue();
        }

        // Defensive verified check
        List<VendorProfile> verifiedVendors = vendors.stream()
                .filter(v -> Boolean.TRUE.equals(v.getVerified()))  // ✅ FIX
                .collect(Collectors.toList());


        // Sorting
        sortVendors(verifiedVendors, sortBy);

        // Pagination
        int fromIndex = Math.min(page * size, verifiedVendors.size());
        int toIndex = Math.min(fromIndex + size, verifiedVendors.size());

        List<VendorProfile> paged = verifiedVendors.subList(fromIndex, toIndex);

        // Map to DTO
        return paged.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    // SORT HELPER
    private void sortVendors(List<VendorProfile> vendors, String sortBy) {

        if (sortBy == null) return;

        switch (sortBy.toLowerCase()) {
            case "businessname" ->
                    vendors.sort(Comparator.comparing(
                            VendorProfile::getBusinessName,
                            String.CASE_INSENSITIVE_ORDER));

            case "city" ->
                    vendors.sort(Comparator.comparing(
                            VendorProfile::getCity,
                            String.CASE_INSENSITIVE_ORDER));

            case "category" ->
                    vendors.sort(Comparator.comparing(
                            VendorProfile::getCategory,
                            String.CASE_INSENSITIVE_ORDER));

            default -> {
                // no sorting
            }
        }
    }


    // ENTITY → DTO MAPPER
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
                .verified(v.getVerified())   // works fine for Boolean
                .imageUrls(v.getImageUrls())
                .videoUrls(v.getVideoUrls())
                .build();
    }

}
