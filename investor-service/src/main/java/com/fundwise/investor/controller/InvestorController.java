package com.fundwise.investor.controller;

import com.fundwise.investor.dto.InvestorRequests.*;
import com.fundwise.investor.entity.*;
import com.fundwise.investor.service.InvestorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class InvestorController {
    private final InvestorService service;

    @PostMapping("/investors") @ResponseStatus(HttpStatus.CREATED) public Investor createInvestor(@Valid @RequestBody InvestorRequest r) { return service.createInvestor(r); }
    @GetMapping("/investors") public List<Investor> getInvestors() { return service.getInvestors(); }
    @GetMapping("/investors/{id}") public Investor getInvestor(@PathVariable Long id) { return service.getInvestor(id); }
    @PutMapping("/investors/{id}") public Investor updateInvestor(@PathVariable Long id, @Valid @RequestBody InvestorRequest r) { return service.updateInvestor(id, r); }
    @DeleteMapping("/investors/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteInvestor(@PathVariable Long id) { service.deleteInvestor(id); }

    @PostMapping("/bank-mandates") @ResponseStatus(HttpStatus.CREATED) public BankMandate createBankMandate(@Valid @RequestBody BankMandateRequest r) { return service.createBankMandate(r); }
    @GetMapping("/bank-mandates") public List<BankMandate> getBankMandates() { return service.getBankMandates(); }
    @GetMapping("/bank-mandates/{id}") public BankMandate getBankMandate(@PathVariable Long id) { return service.getBankMandate(id); }
    @PutMapping("/bank-mandates/{id}") public BankMandate updateBankMandate(@PathVariable Long id, @Valid @RequestBody BankMandateRequest r) { return service.updateBankMandate(id, r); }
    @DeleteMapping("/bank-mandates/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteBankMandate(@PathVariable Long id) { service.deleteBankMandate(id); }

    @PostMapping("/nominees") @ResponseStatus(HttpStatus.CREATED) public Nominee createNominee(@Valid @RequestBody NomineeRequest r) { return service.createNominee(r); }
    @GetMapping("/nominees") public List<Nominee> getNominees() { return service.getNominees(); }
    @GetMapping("/nominees/{id}") public Nominee getNominee(@PathVariable Long id) { return service.getNominee(id); }
    @PutMapping("/nominees/{id}") public Nominee updateNominee(@PathVariable Long id, @Valid @RequestBody NomineeRequest r) { return service.updateNominee(id, r); }
    @DeleteMapping("/nominees/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteNominee(@PathVariable Long id) { service.deleteNominee(id); }

    @PostMapping("/kyc-documents") @ResponseStatus(HttpStatus.CREATED) public KycDocument createKycDocument(@Valid @RequestBody KycDocumentRequest r) { return service.createKycDocument(r); }
    @GetMapping("/kyc-documents") public List<KycDocument> getKycDocuments() { return service.getKycDocuments(); }
    @GetMapping("/kyc-documents/{id}") public KycDocument getKycDocument(@PathVariable Long id) { return service.getKycDocument(id); }
    @PutMapping("/kyc-documents/{id}") public KycDocument updateKycDocument(@PathVariable Long id, @Valid @RequestBody KycDocumentRequest r) { return service.updateKycDocument(id, r); }
    @DeleteMapping("/kyc-documents/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteKycDocument(@PathVariable Long id) { service.deleteKycDocument(id); }
}
