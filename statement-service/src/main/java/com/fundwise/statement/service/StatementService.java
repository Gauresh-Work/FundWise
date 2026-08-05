package com.fundwise.statement.service;

import com.fundwise.statement.dto.StatementRequest;
import com.fundwise.statement.entity.StatementRecord;
import com.fundwise.statement.repository.StatementRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatementService {
    private final StatementRecordRepository repository;
    private final RestClient.Builder restClientBuilder;

    public StatementRecord create(StatementRequest r) {
        StatementRecord e = new StatementRecord();
        e.setFolioId(r.folioId());
        e.setStatementType(r.statementType() == null ? "JSON" : r.statementType());
        e.setGeneratedAt(LocalDateTime.now());
        return repository.save(e);
    }

    public List<StatementRecord> all() {
        return repository.findAll();
    }

    public void delete(Long id) {
        repository.delete(repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Statement not found")));
    }

    public Map<String, Object> folioStatement(Long folioId) {
        try {
            RestClient client = restClientBuilder.build();
            Map<String, Object> folio = client.get().uri("http://folio-service/folios/{id}", folioId).retrieve().body(Map.class);
            Object transactions = client.get().uri("http://transaction-service/transactions?folioId={id}", folioId).retrieve().body(Object.class);
            Map<String, Object> investor = client.get().uri("http://investor-service/investors/{id}", folio.get("investorId")).retrieve().body(Map.class);
            Map<String, Object> scheme = client.get().uri("http://scheme-service/schemes/{id}", folio.get("schemeId")).retrieve().body(Map.class);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("folio", folio);
            result.put("investor", investor);
            result.put("scheme", scheme);
            result.put("transactions", transactions);
            result.put("generatedStatements", repository.findByFolioId(folioId));
            result.put("generatedAt", LocalDateTime.now());
            return result;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "A service required to build this statement is unavailable", ex);
        }
    }
}
