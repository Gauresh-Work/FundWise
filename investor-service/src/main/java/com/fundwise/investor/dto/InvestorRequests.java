package com.fundwise.investor.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public final class InvestorRequests {
    private InvestorRequests() { }

    public record InvestorRequest(@NotBlank @Size(min = 2, max = 100) String fullName,
                                  @NotBlank @Email String email,
                                  @Pattern(regexp = "^$|^[0-9]{10}$", message = "Phone number must contain exactly 10 digits") String phone,
                                  @NotBlank @Pattern(regexp = "^[A-Za-z]{5}[0-9]{4}[A-Za-z]$", message = "PAN must contain 5 letters, 4 digits and 1 final letter") String panNumber,
                                  @NotBlank String status) { }
    public record BankMandateRequest(@NotNull Long investorId, @NotBlank String bankName,
                                     @NotBlank @Pattern(regexp = "^[0-9]{9,18}$", message = "Account number must contain 9 to 18 digits") String accountNumber,
                                     @NotBlank @Pattern(regexp = "^[A-Za-z]{4}0[A-Za-z0-9]{6}$", message = "Enter a valid 11-character IFSC code") String ifscCode,
                                     String accountType) { }
    public record NomineeRequest(@NotNull Long investorId, @NotBlank String fullName,
                                 String relationship, @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal allocationPercentage) { }
    public record KycDocumentRequest(@NotNull Long investorId, @NotBlank String documentType,
                                     @NotBlank @Size(max = 20) String documentNumber,
                                     String documentUrl, @NotBlank String status) { }
}
