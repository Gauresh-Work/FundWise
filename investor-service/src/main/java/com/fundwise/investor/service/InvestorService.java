package com.fundwise.investor.service;

import com.fundwise.investor.dto.InvestorRequests.BankMandateRequest;
import com.fundwise.investor.dto.InvestorRequests.InvestorRequest;
import com.fundwise.investor.dto.InvestorRequests.KycDocumentRequest;
import com.fundwise.investor.dto.InvestorRequests.NomineeRequest;
import com.fundwise.investor.entity.BankMandate;
import com.fundwise.investor.entity.Investor;
import com.fundwise.investor.entity.KycDocument;
import com.fundwise.investor.entity.Nominee;
import com.fundwise.investor.exception.NotFoundException;
import com.fundwise.investor.repository.BankMandateRepository;
import com.fundwise.investor.repository.InvestorRepository;
import com.fundwise.investor.repository.KycDocumentRepository;
import com.fundwise.investor.repository.NomineeRepository;
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

    public Investor createInvestor(InvestorRequest request) {
        return investorRepository.save(investor(request));
    }

    public List<Investor> getInvestors() {
        return investorRepository.findAll();
    }

    public Investor getInvestor(Long id) {
        return investorRepository.findById(id).orElseThrow(() -> new NotFoundException("Investor", id));
    }

    public Investor updateInvestor(Long id, InvestorRequest request) {
        Investor entity = getInvestor(id);
        copy(request, entity);
        return investorRepository.save(entity);
    }

    public void deleteInvestor(Long id) {
        investorRepository.delete(getInvestor(id));
    }

    public BankMandate createBankMandate(BankMandateRequest request) {
        return bankMandateRepository.save(bankMandate(request));
    }

    public List<BankMandate> getBankMandates() {
        return bankMandateRepository.findAll();
    }

    public BankMandate getBankMandate(Long id) {
        return bankMandateRepository.findById(id).orElseThrow(() -> new NotFoundException("Bank mandate", id));
    }

    public BankMandate updateBankMandate(Long id, BankMandateRequest request) {
        BankMandate entity = getBankMandate(id);
        copy(request, entity);
        return bankMandateRepository.save(entity);
    }

    public void deleteBankMandate(Long id) {
        bankMandateRepository.delete(getBankMandate(id));
    }

    public Nominee createNominee(NomineeRequest request) {
        return nomineeRepository.save(nominee(request));
    }

    public List<Nominee> getNominees() {
        return nomineeRepository.findAll();
    }

    public Nominee getNominee(Long id) {
        return nomineeRepository.findById(id).orElseThrow(() -> new NotFoundException("Nominee", id));
    }

    public Nominee updateNominee(Long id, NomineeRequest request) {
        Nominee entity = getNominee(id);
        copy(request, entity);
        return nomineeRepository.save(entity);
    }

    public void deleteNominee(Long id) {
        nomineeRepository.delete(getNominee(id));
    }

    public KycDocument createKycDocument(KycDocumentRequest request) {
        validateDocument(request);
        return kycDocumentRepository.save(kycDocument(request));
    }

    public List<KycDocument> getKycDocuments() {
        return kycDocumentRepository.findAll();
    }

    public KycDocument getKycDocument(Long id) {
        return kycDocumentRepository.findById(id).orElseThrow(() -> new NotFoundException("KYC document", id));
    }

    public KycDocument updateKycDocument(Long id, KycDocumentRequest request) {
        validateDocument(request);
        KycDocument entity = getKycDocument(id);
        copy(request, entity);
        return kycDocumentRepository.save(entity);
    }

    public void deleteKycDocument(Long id) {
        kycDocumentRepository.delete(getKycDocument(id));
    }

    private Investor investor(InvestorRequest r) {
        Investor e = new Investor();
        copy(r, e);
        return e;
    }

    private void copy(InvestorRequest r, Investor e) {
        e.setFullName(r.fullName());
        e.setEmail(r.email());
        e.setPhone(r.phone());
        e.setPanNumber(r.panNumber());
        e.setStatus(r.status());
    }

    private BankMandate bankMandate(BankMandateRequest r) {
        BankMandate e = new BankMandate();
        copy(r, e);
        return e;
    }

    private void copy(BankMandateRequest r, BankMandate e) {
        e.setInvestorId(r.investorId());
        e.setBankName(r.bankName());
        e.setAccountNumber(r.accountNumber());
        e.setIfscCode(r.ifscCode());
        e.setAccountType(r.accountType());
    }

    private Nominee nominee(NomineeRequest r) {
        Nominee e = new Nominee();
        copy(r, e);
        return e;
    }

    private void copy(NomineeRequest r, Nominee e) {
        e.setInvestorId(r.investorId());
        e.setFullName(r.fullName());
        e.setRelationship(r.relationship());
        e.setAllocationPercentage(r.allocationPercentage());
    }

    private KycDocument kycDocument(KycDocumentRequest r) {
        KycDocument e = new KycDocument();
        copy(r, e);
        return e;
    }

    private void copy(KycDocumentRequest r, KycDocument e) {
        e.setInvestorId(r.investorId());
        e.setDocumentType(r.documentType());
        e.setDocumentNumber(r.documentNumber());
        e.setDocumentUrl(r.documentUrl());
        e.setStatus(r.status());
    }

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
}
