package com.fundwise.investor.repository;
import com.fundwise.investor.entity.Investor;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InvestorRepository extends JpaRepository<Investor, Long> { }
