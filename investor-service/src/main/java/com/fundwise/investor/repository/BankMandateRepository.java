package com.fundwise.investor.repository;
import com.fundwise.investor.entity.BankMandate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BankMandateRepository extends JpaRepository<BankMandate, Long> { List<BankMandate> findByInvestorId(Long investorId); }
