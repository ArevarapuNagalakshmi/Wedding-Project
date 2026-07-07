package com.eventplatform.dto.service;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicePackageDto {

    private Long id;
    private Long vendorId;
    private String name;
    private BigDecimal price;
    private String description;
    private List<String> images;
}
