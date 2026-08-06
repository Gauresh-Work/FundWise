package com.fundwise.investor.service;

import com.fundwise.investor.dto.InvestorRequests.*;
import com.fundwise.investor.entity.*;
import com.fundwise.investor.exception.NotFoundException;
import com.fundwise.investor.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestorService {
    private final InvestorRepository investorRepository;
    private final BankMandateRepository bankMandateRepository;
    private final NomineeRepository nomineeRepository;
    private final KycDocumentRepository kycDocumentRepository;

    public Investor createInvestor(InvestorRequest request) { return investorRepository.save(investor(request)); }
    public List<Investor> getInvestors() { return investorRepository.findAll(); }
    public Investor getInvestor(Long id) { return investorRepository.findById(id).orElseThrow(() -> new NotFoundException("Investor", id)); }
    public Investor updateInvestor(Long id, InvestorRequest request) { Investor entity = getInvestor(id); copy(request, entity); return investorRepository.save(entity); }
    public Investor approveOnboarding(Long id) {
        Investor investor = getInvestor(id);
        List<KycDocument> documents = kycDocumentRepository.findByInvestorId(id);
        List<BankMandate> mandates = bankMandateRepository.findByInvestorId(id);
        List<Nominee> nominees = nomineeRepository.findByInvestorId(id);
        boolean kycVerified = documents.size() >= 2 && documents.stream().allMatch(document -> "VERIFIED".equalsIgnoreCase(document.getStatus()));
        boolean bankVerified = mandates.stream().anyMatch(mandate -> "VERIFIED".equalsIgnoreCase(mandate.getStatus()));
        if (!kycVerified || !bankVerified || nominees.isEmpty()) {
            java.util.List<String> missing = new java.util.ArrayList<>();
            if (!kycVerified) missing.add("two verified KYC documents");
            if (!bankVerified) missing.add("a verified bank mandate");
            if (nominees.isEmpty()) missing.add("a nominee");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot approve onboarding: add " + String.join(", ", missing));
        }
        investor.setStatus("ACTIVE");
        return investorRepository.save(investor);
    }
    public void deleteInvestor(Long id) { investorRepository.delete(getInvestor(id)); }

    public BankMandate createBankMandate(BankMandateRequest request) { BankMandate saved = bankMandateRepository.save(bankMandate(request)); refreshOnboardingStatus(saved.getInvestorId()); return saved; }
    public List<BankMandate> getBankMandates() { return bankMandateRepository.findAll(); }
    public List<BankMandate> getBankMandatesByInvestor(Long investorId) { return bankMandateRepository.findByInvestorId(investorId); }
    public BankMandate getBankMandate(Long id) { return bankMandateRepository.findById(id).orElseThrow(() -> new NotFoundException("Bank mandate", id)); }
    public BankMandate updateBankMandate(Long id, BankMandateRequest request) { BankMandate entity = getBankMandate(id); copy(request, entity); BankMandate saved = bankMandateRepository.save(entity); refreshOnboardingStatus(saved.getInvestorId()); return saved; }
    public void deleteBankMandate(Long id) { bankMandateRepository.delete(getBankMandate(id)); }

    public Nominee createNominee(NomineeRequest request) { Nominee saved = nomineeRepository.save(nominee(request)); refreshOnboardingStatus(saved.getInvestorId()); return saved; }
    public List<Nominee> getNominees() { return nomineeRepository.findAll(); }
    public List<Nominee> getNomineesByInvestor(Long investorId) { return nomineeRepository.findByInvestorId(investorId); }
    public Nominee getNominee(Long id) { return nomineeRepository.findById(id).orElseThrow(() -> new NotFoundException("Nominee", id)); }
    public Nominee updateNominee(Long id, NomineeRequest request) { Nominee entity = getNominee(id); copy(request, entity); Nominee saved = nomineeRepository.save(entity); refreshOnboardingStatus(saved.getInvestorId()); return saved; }
    public void deleteNominee(Long id) { nomineeRepository.delete(getNominee(id)); }

    public KycDocument createKycDocument(KycDocumentRequest request) { validateDocument(request); KycDocument saved = kycDocumentRepository.save(kycDocument(request)); refreshOnboardingStatus(saved.getInvestorId()); return saved; }
    public List<KycDocument> getKycDocuments() { return kycDocumentRepository.findAll(); }
    public List<KycDocument> getKycDocumentsByInvestor(Long investorId) { return kycDocumentRepository.findByInvestorId(investorId); }
    public KycDocument getKycDocument(Long id) { return kycDocumentRepository.findById(id).orElseThrow(() -> new NotFoundException("KYC document", id)); }
    public KycDocument updateKycDocument(Long id, KycDocumentRequest request) { validateDocument(request); KycDocument entity = getKycDocument(id); copy(request, entity); KycDocument saved = kycDocumentRepository.save(entity); refreshOnboardingStatus(saved.getInvestorId()); return saved; }
    public void deleteKycDocument(Long id) { kycDocumentRepository.delete(getKycDocument(id)); }

    private Investor investor(InvestorRequest r) { Investor e = new Investor(); copy(r, e); return e; }
    private void copy(InvestorRequest r, Investor e) { e.setFullName(r.fullName()); e.setEmail(r.email()); e.setPhone(r.phone()); e.setPanNumber(r.panNumber()); e.setStatus("PENDING_KYC".equalsIgnoreCase(r.status()) ? "PENDING" : r.status()); }
    private BankMandate bankMandate(BankMandateRequest r) { BankMandate e = new BankMandate(); copy(r, e); return e; }
    private void copy(BankMandateRequest r, BankMandate e) { e.setInvestorId(r.investorId()); e.setBankName(r.bankName()); e.setAccountNumber(r.accountNumber()); e.setIfscCode(r.ifscCode()); e.setAccountType(r.accountType()); e.setStatus(r.status()); }
    private Nominee nominee(NomineeRequest r) { Nominee e = new Nominee(); copy(r, e); return e; }
    private void copy(NomineeRequest r, Nominee e) { e.setInvestorId(r.investorId()); e.setFullName(r.fullName()); e.setRelationship(r.relationship()); e.setAllocationPercentage(r.allocationPercentage()); }
    private KycDocument kycDocument(KycDocumentRequest r) { KycDocument e = new KycDocument(); copy(r, e); return e; }
    private void copy(KycDocumentRequest r, KycDocument e) { e.setInvestorId(r.investorId()); e.setDocumentType(r.documentType()); e.setDocumentNumber(r.documentNumber()); e.setDocumentUrl(r.documentUrl()); e.setStatus(r.status()); }
    private void validateDocument(KycDocumentRequest r) {
        String type = r.documentType().trim().toUpperCase();
        String number = r.documentNumber().replaceAll("\\s", "").toUpperCase();
        boolean valid = switch (type) {
            case "AADHAAR" -> number.matches("^[0-9]{12}$");
            case "PAN" -> number.matches("^[A-Z]{5}[0-9]{4}[A-Z]$");
            case "PASSPORT" -> number.matches("^[A-Z][0-9]{7}$");
            case "VOTER ID" -> number.matches("^[A-Z]{3}[0-9]{7}$");
            default -> false;
        };
        if (!valid) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + type + " document number");
    }
    private void refreshOnboardingStatus(Long investorId) {
        Investor investor = getInvestor(investorId);
        List<KycDocument> documents = kycDocumentRepository.findByInvestorId(investorId);
        List<BankMandate> mandates = bankMandateRepository.findByInvestorId(investorId);
        boolean kycVerified = documents.size() >= 2 && documents.stream().allMatch(document -> "VERIFIED".equalsIgnoreCase(document.getStatus()));
        boolean bankVerified = mandates.stream().anyMatch(mandate -> "VERIFIED".equalsIgnoreCase(mandate.getStatus()));
        boolean hasNominee = !nomineeRepository.findByInvestorId(investorId).isEmpty();
        if (!kycVerified) investor.setStatus("PENDING");
        else if (bankVerified && hasNominee) investor.setStatus("READY_FOR_APPROVAL");
        else investor.setStatus("KYC_VERIFIED");
        investorRepository.save(investor);
    }
}
