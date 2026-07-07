package com.eventplatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "customer_profiles", uniqueConstraints = @UniqueConstraint(columnNames = "owner_id"))
public class CustomerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private User owner;

    private String city;

    private LocalDate weddingDate;

    private BigDecimal budget;

    private String weddingType;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
