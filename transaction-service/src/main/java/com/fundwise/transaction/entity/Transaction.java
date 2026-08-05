package com.fundwise.transaction.entity;
import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import java.time.LocalDate;
@Entity @Table(name="fund_transactions") @Getter @Setter @NoArgsConstructor @AllArgsConstructor public class Transaction {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private Long folioId;
 @Column(nullable=false) private String transactionType;
 @Column(nullable=false) private LocalDate transactionDate;
 @Column(nullable=false) private BigDecimal amount;
 private BigDecimal nav; private BigDecimal units; private Long targetSchemeId; @Column(nullable=false) private String status;
}
