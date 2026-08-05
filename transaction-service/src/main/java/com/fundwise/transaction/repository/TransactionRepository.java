package com.fundwise.transaction.repository;
import com.fundwise.transaction.entity.Transaction; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface TransactionRepository extends JpaRepository<Transaction,Long>{ List<Transaction> findByFolioId(Long folioId); }
