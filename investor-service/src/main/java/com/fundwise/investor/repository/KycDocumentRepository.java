package com.fundwise.investor.repository;
import com.fundwise.investor.entity.KycDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface KycDocumentRepository extends JpaRepository<KycDocument, Long> { List<KycDocument> findByInvestorId(Long investorId); }
