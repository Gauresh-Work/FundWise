package com.fundwise.statement.config;

import com.fundwise.statement.entity.StatementRecord;
import com.fundwise.statement.repository.StatementRecordRepository;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataInitializer {

    @Bean
    CommandLineRunner seedStatements(StatementRecordRepository statements) {
        return args -> {
            if (statements.count() > 0) return;
            statements.save(new StatementRecord(null, 1L, LocalDateTime.of(2026, 8, 4, 10, 30), "PORTFOLIO_SUMMARY"));
            statements.save(new StatementRecord(null, 2L, LocalDateTime.of(2026, 8, 4, 10, 35), "TRANSACTION_HISTORY"));
        };
    }
}
