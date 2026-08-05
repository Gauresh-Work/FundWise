package com.fundwise.scheme.repository;
import com.fundwise.scheme.entity.NavHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface NavHistoryRepository extends JpaRepository<NavHistory, Long> {
    List<NavHistory> findBySchemeIdOrderByNavDateDesc(Long schemeId);
}
