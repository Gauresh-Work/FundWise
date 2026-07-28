package com.fundwise.investor.repository;
import com.fundwise.investor.entity.KycDocument;
import org.springframework.data.jpa.repository.JpaRepository;
public interface KycDocumentRepository extends JpaRepository<KycDocument, Long> { }
