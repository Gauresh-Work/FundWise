package com.fundwise.investor.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kyc_documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class KycDocument {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long investorId;
    @Column(nullable = false)
    private String documentType;
    @Column(nullable = false)
    private String documentNumber;
    private String documentUrl;
    @Column(nullable = false)
    private String status;
}
