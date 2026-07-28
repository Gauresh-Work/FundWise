package com.fundwise.scheme.repository;
import com.fundwise.scheme.entity.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SchemeRepository extends JpaRepository<Scheme, Long> { }
