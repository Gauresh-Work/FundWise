package com.fundwise.transaction.service;

import com.fundwise.transaction.dto.TransactionRequest;
import com.fundwise.transaction.entity.Transaction;
import com.fundwise.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;

    public Transaction create(TransactionRequest r) {
        return repository.save(copy(r, new Transaction()));
    }

    public List<Transaction> all(Long folioId) {
        return folioId == null ? repository.findAll() : repository.findByFolioId(folioId);
    }

    public Transaction one(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
    }

    public Transaction update(Long id, TransactionRequest r) {
        return repository.save(copy(r, one(id)));
    }

    public void delete(Long id) {
        repository.delete(one(id));
    }

    private Transaction copy(TransactionRequest r, Transaction e) {
        e.setFolioId(r.folioId());
        e.setTransactionType(r.transactionType());
        e.setTransactionDate(r.transactionDate());
        e.setAmount(r.amount());
        e.setNav(r.nav());
        e.setUnits(r.units());
        e.setTargetSchemeId(r.targetSchemeId());
        e.setStatus(r.status());
        return e;
    }
}
