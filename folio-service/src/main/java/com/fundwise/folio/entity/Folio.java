package com.fundwise.folio.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "folios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Folio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long investorId;
    @Column(nullable = false)
    private Long schemeId;
    @Column(nullable = false, unique = true)
    private String folioNumber;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private BigDecimal currentUnits;
    @Column(nullable = false)
    private BigDecimal averageNav;
    @Column(nullable = false)
    private BigDecimal currentValue;
}
