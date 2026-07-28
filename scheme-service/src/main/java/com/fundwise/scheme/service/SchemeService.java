package com.fundwise.scheme.service;

import com.fundwise.scheme.dto.SchemeRequests.NavHistoryRequest;
import com.fundwise.scheme.dto.SchemeRequests.SchemeRequest;
import com.fundwise.scheme.entity.NavHistory;
import com.fundwise.scheme.entity.Scheme;
import com.fundwise.scheme.exception.DuplicateResourceException;
import com.fundwise.scheme.exception.NotFoundException;
import com.fundwise.scheme.repository.NavHistoryRepository;
import com.fundwise.scheme.repository.SchemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SchemeService {

    private final SchemeRepository schemeRepository;
    private final NavHistoryRepository navHistoryRepository;

    public Scheme create(SchemeRequest request) {
        if (schemeRepository.existsBySchemeCode(request.schemeCode())) {
            throw new DuplicateResourceException(
                    "Scheme code " + request.schemeCode() + " already exists"
            );
        }

        Scheme scheme = new Scheme();
        copySchemeRequest(request, scheme);
        return schemeRepository.save(scheme);
    }

    @Transactional(readOnly = true)
    public List<Scheme> getAll(String status, String type) {
        if (status != null && !status.isBlank()) {
            return schemeRepository.findByStatusIgnoreCase(status);
        }

        if (type != null && !type.isBlank()) {
            return schemeRepository.findBySchemeTypeIgnoreCase(type);
        }

        return schemeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Scheme get(Long id) {
        return schemeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Scheme", id));
    }

    public Scheme update(Long id, SchemeRequest request) {
        Scheme scheme = get(id);

        if (schemeRepository.existsBySchemeCodeAndIdNot(request.schemeCode(), id)) {
            throw new DuplicateResourceException(
                    "Scheme code " + request.schemeCode() + " already exists"
            );
        }

        copySchemeRequest(request, scheme);
        return schemeRepository.save(scheme);
    }

    public void delete(Long id) {
        get(id);

        // In production, Folio Service should first confirm that no folios
        // exist for this scheme. For this student project, delete related NAV rows.
        navHistoryRepository.deleteBySchemeId(id);
        schemeRepository.deleteById(id);
    }

    public NavHistory saveNav(Long schemeId, NavHistoryRequest request) {
        Scheme scheme = get(schemeId);

        if (!"ACTIVE".equalsIgnoreCase(scheme.getStatus())) {
            throw new IllegalStateException("NAV can only be maintained for an ACTIVE scheme");
        }

        NavHistory navHistory = navHistoryRepository
                .findBySchemeIdAndNavDate(schemeId, request.navDate())
                .orElseGet(NavHistory::new);

        navHistory.setSchemeId(schemeId);
        navHistory.setNavDate(request.navDate());
        navHistory.setNavValue(request.navValue());

        return navHistoryRepository.save(navHistory);
    }

    @Transactional(readOnly = true)
    public List<NavHistory> getNavHistory(Long schemeId) {
        get(schemeId);
        return navHistoryRepository.findBySchemeIdOrderByNavDateDesc(schemeId);
    }

    @Transactional(readOnly = true)
    public NavHistory getLatestNav(Long schemeId) {
        get(schemeId);

        return navHistoryRepository.findFirstBySchemeIdOrderByNavDateDesc(schemeId)
                .orElseThrow(() -> new NotFoundException("NAV history for scheme", schemeId));
    }

    private void copySchemeRequest(SchemeRequest request, Scheme scheme) {
        scheme.setSchemeCode(request.schemeCode().trim().toUpperCase());
        scheme.setSchemeName(request.schemeName().trim());
        scheme.setSchemeType(request.schemeType().trim().toUpperCase());
        scheme.setRiskLevel(request.riskLevel().trim().toUpperCase());
        scheme.setLaunchDate(request.launchDate());
        scheme.setMinInvestment(request.minInvestment());
        scheme.setExpenseRatio(request.expenseRatio());
        scheme.setStatus(request.status().trim().toUpperCase());
    }
}