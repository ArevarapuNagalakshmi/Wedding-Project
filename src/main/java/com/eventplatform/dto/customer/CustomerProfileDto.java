package com.eventplatform.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfileDto {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String city;
    private LocalDate weddingDate;
    private BigDecimal budget;
    private String weddingType;
    private String notes;
}
