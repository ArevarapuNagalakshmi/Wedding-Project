package com.eventplatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private String businessName;
    private String category;
    private String description;
    private String city;
    private String address;
    private String pricingRange;
    private String responseTime;
    private String gstin;
    private Double rating;

    @Column(nullable = false)
    private Boolean verified;   // 👈 MUST exist

    @ElementCollection
    @CollectionTable(
            name = "vendor_category_tags",
            joinColumns = @JoinColumn(name = "vendor_id")
    )
    @Column(name = "category_tag")
    private List<String> categoryTags;

    @ElementCollection
    @CollectionTable(
            name = "vendor_images",
            joinColumns = @JoinColumn(name = "vendor_id")
    )
    @Column(name = "image_url")
    private List<String> imageUrls;

    @ElementCollection
    @CollectionTable(
            name = "vendor_videos",
            joinColumns = @JoinColumn(name = "vendor_id")
    )
    @Column(name = "video_url")
    private List<String> videoUrls;
}
