package com.fundwise.scheme.repository;

import com.fundwise.scheme.entity.NavHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NavHistoryRepository extends JpaRepository<NavHistory, Long> {

    List<NavHistory> findBySchemeIdOrderByNavDateDesc(Long schemeId);

    Optional<NavHistory> findBySchemeIdAndNavDate(Long schemeId, LocalDate navDate);

    Optional<NavHistory> findFirstBySchemeIdOrderByNavDateDesc(Long schemeId);

    void deleteBySchemeId(Long schemeId);
}