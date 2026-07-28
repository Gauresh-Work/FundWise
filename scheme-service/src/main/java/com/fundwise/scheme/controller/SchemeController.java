package com.fundwise.scheme.controller;

import com.fundwise.scheme.dto.SchemeRequests.NavHistoryRequest;
import com.fundwise.scheme.dto.SchemeRequests.SchemeRequest;
import com.fundwise.scheme.entity.NavHistory;
import com.fundwise.scheme.entity.Scheme;
import com.fundwise.scheme.service.SchemeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schemes")
@RequiredArgsConstructor
public class SchemeController {

    private final SchemeService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Scheme create(@Valid @RequestBody SchemeRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<Scheme> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type
    ) {
        return service.getAll(status, type);
    }

    @GetMapping("/{id}")
    public Scheme get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Scheme update(
            @PathVariable Long id,
            @Valid @RequestBody SchemeRequest request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/{id}/nav-history")
    @ResponseStatus(HttpStatus.CREATED)
    public NavHistory saveNav(
            @PathVariable Long id,
            @Valid @RequestBody NavHistoryRequest request
    ) {
        return service.saveNav(id, request);
    }

    @GetMapping("/{id}/nav-history")
    public List<NavHistory> getNavHistory(@PathVariable Long id) {
        return service.getNavHistory(id);
    }

    @GetMapping("/{id}/nav-history/latest")
    public NavHistory getLatestNav(@PathVariable Long id) {
        return service.getLatestNav(id);
    }
}