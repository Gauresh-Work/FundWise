package com.fundwise.scheme.repository;

import com.fundwise.scheme.entity.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchemeRepository extends JpaRepository<Scheme, Long> {

    boolean existsBySchemeCode(String schemeCode);

    boolean existsBySchemeCodeAndIdNot(String schemeCode, Long id);

    List<Scheme> findByStatusIgnoreCase(String status);

    List<Scheme> findBySchemeTypeIgnoreCase(String schemeType);
}