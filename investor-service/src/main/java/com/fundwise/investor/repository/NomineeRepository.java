package com.fundwise.investor.repository;
import com.fundwise.investor.entity.Nominee;
import org.springframework.data.jpa.repository.JpaRepository;
public interface NomineeRepository extends JpaRepository<Nominee, Long> { }
