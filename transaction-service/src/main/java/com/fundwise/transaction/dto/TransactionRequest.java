package com.fundwise.transaction.dto;
import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.time.LocalDate;
public record TransactionRequest(@NotNull Long folioId,@NotBlank String transactionType,@NotNull LocalDate transactionDate,@NotNull @DecimalMin("0.0") BigDecimal amount,@DecimalMin("0.0") BigDecimal nav,@DecimalMin("0.0") BigDecimal units,Long targetSchemeId,@NotBlank String status){}
