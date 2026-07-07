package com.eventplatform.service.search;

import java.util.List;
import com.eventplatform.dto.vendor.VendorProfileDto;

public interface SearchService {

    // Simple search (used by current UI / MVP)
    List<VendorProfileDto> search(String city, String category);

    //  Advanced search (future-ready: pagination + sorting)
    List<VendorProfileDto> search(
            String city,
            String category,
            int page,
            int size,
            String sortBy
    );
}
