package com.fundwise.scheme.controller;

import com.fundwise.scheme.dto.SchemeRequests.*;
import com.fundwise.scheme.entity.*;
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

    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Scheme create(@Valid @RequestBody SchemeRequest r) { return service.create(r); }
    @GetMapping public List<Scheme> getAll() { return service.getAll(); }
    @GetMapping("/{id}") public Scheme get(@PathVariable Long id) { return service.get(id); }
    @PutMapping("/{id}") public Scheme update(@PathVariable Long id, @Valid @RequestBody SchemeRequest r) { return service.update(id, r); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.delete(id); }
    @PostMapping("/{id}/nav-history") @ResponseStatus(HttpStatus.CREATED) public NavHistory addNavHistory(@PathVariable Long id, @Valid @RequestBody NavHistoryRequest r) { return service.addNavHistory(id, r); }
    @GetMapping("/{id}/nav-history") public List<NavHistory> getNavHistory(@PathVariable Long id) { return service.getNavHistory(id); }
}
