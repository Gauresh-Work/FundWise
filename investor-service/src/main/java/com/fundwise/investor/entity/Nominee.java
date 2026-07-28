package com.fundwise.investor.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "nominees")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Nominee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long investorId;
    @Column(nullable = false)
    private String fullName;
    private String relationship;
    private BigDecimal allocationPercentage;
}
