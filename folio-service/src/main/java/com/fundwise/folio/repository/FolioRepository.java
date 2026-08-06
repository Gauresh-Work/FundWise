package com.fundwise.folio.repository;
import com.fundwise.folio.entity.Folio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface FolioRepository extends JpaRepository<Folio, Long> { List<Folio> findByInvestorId(Long investorId); }
