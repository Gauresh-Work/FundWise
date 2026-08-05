package com.fundwise.scheme.service;

import com.fundwise.scheme.dto.SchemeRequests.*;
import com.fundwise.scheme.entity.*;
import com.fundwise.scheme.exception.NotFoundException;
import com.fundwise.scheme.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchemeService {
    private final SchemeRepository schemeRepository;
    private final NavHistoryRepository navHistoryRepository;

    public Scheme create(SchemeRequest request) { return schemeRepository.save(scheme(request)); }
    public List<Scheme> getAll() { return schemeRepository.findAll(); }
    public Scheme get(Long id) { return schemeRepository.findById(id).orElseThrow(() -> new NotFoundException("Scheme", id)); }
    public Scheme update(Long id, SchemeRequest request) { Scheme entity = get(id); copy(request, entity); return schemeRepository.save(entity); }
    public void delete(Long id) { schemeRepository.delete(get(id)); }

    public NavHistory addNavHistory(Long schemeId, NavHistoryRequest request) {
        get(schemeId);
        NavHistory navHistory = new NavHistory();
        navHistory.setSchemeId(schemeId); navHistory.setNavDate(request.navDate()); navHistory.setNav(request.nav());
        return navHistoryRepository.save(navHistory);
    }
    public List<NavHistory> getNavHistory(Long schemeId) { get(schemeId); return navHistoryRepository.findBySchemeIdOrderByNavDateDesc(schemeId); }

    private Scheme scheme(SchemeRequest r) { Scheme entity = new Scheme(); copy(r, entity); return entity; }
    private void copy(SchemeRequest r, Scheme e) { e.setSchemeCode(r.schemeCode()); e.setName(r.name()); e.setSchemeType(r.schemeType()); e.setExpenseRatio(r.expenseRatio()); e.setCurrentNav(r.currentNav()); e.setStatus(r.status()); }
}
