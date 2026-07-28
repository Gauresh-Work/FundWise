package com.fundwise.investor.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public final class InvestorRequests {
    private InvestorRequests() { }

    public record InvestorRequest(@NotBlank String fullName, @NotBlank @Email String email,
                                  String phone, @NotBlank String panNumber, @NotBlank String status) { }
    public record BankMandateRequest(@NotNull Long investorId, @NotBlank String bankName,
                                     @NotBlank String accountNumber, @NotBlank String ifscCode, String accountType) { }
    public record NomineeRequest(@NotNull Long investorId, @NotBlank String fullName,
                                 String relationship, @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal allocationPercentage) { }
    public record KycDocumentRequest(@NotNull Long investorId, @NotBlank String documentType,
                                     @NotBlank String documentNumber, String documentUrl, @NotBlank String status) { }
}
