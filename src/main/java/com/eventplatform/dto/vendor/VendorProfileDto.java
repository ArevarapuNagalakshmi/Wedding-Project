package com.eventplatform.dto.vendor;
import lombok.*;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorProfileDto {

    private Long id;

    private String businessName;

    private String category;
    private String description;
    private String city;
    private String address;
    private String pricingRange;
    private String responseTime;
    private List<String> categoryTags;
    private Double rating;
    private Boolean verified;
    private String gstin;
    private List<String> imageUrls;
    private List<String> videoUrls;
}

