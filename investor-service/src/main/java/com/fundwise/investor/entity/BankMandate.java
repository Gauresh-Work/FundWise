package com.fundwise.investor.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank_mandates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BankMandate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long investorId;
    @Column(nullable = false)
    private String bankName;
    @Column(nullable = false)
    private String accountNumber;
    @Column(nullable = false)
    private String ifscCode;
    private String accountType;
    private String status = "PENDING";
}
