package com.fundwise.scheme.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "nav_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class NavHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long schemeId;
    @Column(nullable = false)
    private LocalDate navDate;
    @Column(nullable = false)
    private BigDecimal nav;
}
