package com.fundwise.scheme.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class SchemeRequests {
    private SchemeRequests() { }
    public record SchemeRequest(@NotBlank String schemeCode, @NotBlank String name, @NotBlank String schemeType,
                                @DecimalMin("0.0") BigDecimal expenseRatio, @DecimalMin("0.0") BigDecimal currentNav,
                                @NotBlank String status) { }
    public record NavHistoryRequest(@NotNull LocalDate navDate, @NotNull @DecimalMin("0.0") BigDecimal nav) { }
}
