package com.fundwise.investor.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "investors")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Investor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false, unique = true)
    private String email;
    private String phone;
    @Column(nullable = false, unique = true)
    private String panNumber;
    @Column(nullable = false)
    private String status;
}
