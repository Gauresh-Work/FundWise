package com.fundwise.scheme.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "schemes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Scheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheme_id")
    private Long id;

    @Column(name = "scheme_code", nullable = false, unique = true, length = 30)
    private String schemeCode;

    @Column(name = "scheme_name", nullable = false, length = 150)
    private String schemeName;

    @Column(name = "scheme_type", nullable = false, length = 30)
    private String schemeType; // EQUITY, DEBT, HYBRID

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel; // LOW, MODERATE, HIGH, VERY_HIGH

    @Column(name = "launch_date", nullable = false)
    private LocalDate launchDate;

    @Column(name = "min_investment", nullable = false, precision = 15, scale = 2)
    private BigDecimal minInvestment;

    @Column(name = "expense_ratio", nullable = false, precision = 5, scale = 2)
    private BigDecimal expenseRatio; // percentage, e.g. 1.25

    @Column(nullable = false, length = 20)
    private String status; // ACTIVE, INACTIVE, CLOSED

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}