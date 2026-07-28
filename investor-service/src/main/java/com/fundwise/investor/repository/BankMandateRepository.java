package com.fundwise.investor.repository;
import com.fundwise.investor.entity.BankMandate;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BankMandateRepository extends JpaRepository<BankMandate, Long> { }
