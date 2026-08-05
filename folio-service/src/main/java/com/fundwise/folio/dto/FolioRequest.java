package com.fundwise.folio.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record FolioRequest(@NotNull Long investorId, @NotNull Long schemeId, @NotBlank String folioNumber,
                           @NotBlank String status, @NotNull @DecimalMin("0.0") BigDecimal currentUnits) { }
