package com.fundwise.scheme.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "schemes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Scheme {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String schemeCode;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String schemeType;
    private BigDecimal expenseRatio;
    private BigDecimal currentNav;
    @Column(nullable = false)
    private String status;
}
