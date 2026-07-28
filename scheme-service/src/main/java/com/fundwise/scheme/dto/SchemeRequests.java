package com.fundwise.scheme.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class SchemeRequests {

    private SchemeRequests() {
    }

    public record SchemeRequest(
            @NotBlank @Size(max = 30) String schemeCode,
            @NotBlank @Size(max = 150) String schemeName,
            @NotBlank String schemeType,
            @NotBlank String riskLevel,
            @NotNull @PastOrPresent LocalDate launchDate,
            @NotNull @DecimalMin(value = "0.01") BigDecimal minInvestment,
            @NotNull @DecimalMin(value = "0.00") @DecimalMax(value = "100.00")
            BigDecimal expenseRatio,
            @NotBlank String status
    ) {
    }

    public record NavHistoryRequest(
            @NotNull @PastOrPresent LocalDate navDate,
            @NotNull @DecimalMin(value = "0.0001") BigDecimal navValue
    ) {
    }
}