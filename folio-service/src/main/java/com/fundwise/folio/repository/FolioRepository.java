package com.fundwise.folio.repository;
import com.fundwise.folio.entity.Folio;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FolioRepository extends JpaRepository<Folio, Long> { }
