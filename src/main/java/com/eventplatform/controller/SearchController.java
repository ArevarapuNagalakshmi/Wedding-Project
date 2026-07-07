package com.eventplatform.controller;
import com.eventplatform.dto.vendor.VendorProfileDto;
import com.eventplatform.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;


    // SIMPLE SEARCH (MVP)
    // Example: /api/search?city=Hyderabad&category=Photographer
    @GetMapping
    public ResponseEntity<List<VendorProfileDto>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category) {

        return ResponseEntity.ok(
                searchService.search(city, category)
        );
    }


    // ADVANCED SEARCH (pagination + sorting)
    // Example:
    // /api/search/advanced?city=Hyderabad&category=Photographer&page=0&size=10&sortBy=businessName
    @GetMapping("/advanced")
    public ResponseEntity<List<VendorProfileDto>> advancedSearch(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "businessName") String sortBy) {

        return ResponseEntity.ok(
                searchService.search(city, category, page, size, sortBy)
        );
    }


    // SEARCH BY CITY ONLY
    // Example: /api/search/city/Hyderabad

    @GetMapping("/city/{city}")
    public ResponseEntity<List<VendorProfileDto>> searchByCity(
            @PathVariable String city) {

        return ResponseEntity.ok(
                searchService.search(city, null)
        );
    }


    // SEARCH BY CATEGORY ONLY
    // Example: /api/search/category/Photographer
    @GetMapping("/category/{category}")
    public ResponseEntity<List<VendorProfileDto>> searchByCategory(
            @PathVariable String category) {

        return ResponseEntity.ok(
                searchService.search(null, category)
        );
    }


}
