package com.fundwise.statement.controller;

import com.fundwise.statement.dto.StatementRequest;
import com.fundwise.statement.entity.StatementRecord;
import com.fundwise.statement.service.StatementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/statements")
@RequiredArgsConstructor
public class StatementController {
    private final StatementService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StatementRecord create(@Valid @RequestBody StatementRequest r) {
        return service.create(r);
    }

    @GetMapping
    public List<StatementRecord> all() {
        return service.all();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/folio/{folioId}")
    public Map<String, Object> folioStatement(@PathVariable Long folioId) {
        return service.folioStatement(folioId);
    }
}
