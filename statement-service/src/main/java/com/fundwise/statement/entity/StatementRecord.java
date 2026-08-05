package com.fundwise.statement.entity;
import jakarta.persistence.*; import lombok.*; import java.time.LocalDateTime;
@Entity @Table(name="statement_records") @Getter @Setter @NoArgsConstructor @AllArgsConstructor public class StatementRecord {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private Long folioId;
 @Column(nullable=false) private LocalDateTime generatedAt;
 private String statementType;
}
