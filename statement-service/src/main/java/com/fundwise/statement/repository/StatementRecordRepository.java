package com.fundwise.statement.repository;
import com.fundwise.statement.entity.StatementRecord; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface StatementRecordRepository extends JpaRepository<StatementRecord,Long>{List<StatementRecord> findByFolioId(Long folioId);}
