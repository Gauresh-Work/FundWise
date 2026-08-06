package com.fundwise.investor.repository;
import com.fundwise.investor.entity.Nominee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface NomineeRepository extends JpaRepository<Nominee, Long> { List<Nominee> findByInvestorId(Long investorId); }
