package com.fundwise.scheme.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "nav_history",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_nav_scheme_date",
                columnNames = {"scheme_id", "nav_date"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NavHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nav_id")
    private Long id;

    @Column(name = "scheme_id", nullable = false)
    private Long schemeId;

    @Column(name = "nav_date", nullable = false)
    private LocalDate navDate;

    @Column(name = "nav_value", nullable = false, precision = 15, scale = 4)
    private BigDecimal navValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}