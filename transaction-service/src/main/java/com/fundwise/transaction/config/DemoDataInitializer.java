package com.fundwise.transaction.config;

import com.fundwise.transaction.entity.Transaction;
import com.fundwise.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataInitializer {

    @Bean
    CommandLineRunner seedTransactions(TransactionRepository transactions) {
        return args -> {
            if (transactions.count() > 0) return;
            transactions.save(new Transaction(null, 1L, "PURCHASE", LocalDate.of(2025, 1, 10), new BigDecimal("100000.00"), new BigDecimal("158.2500"), new BigDecimal("631.9115"), null, "COMPLETED"));
            transactions.save(new Transaction(null, 1L, "SIP", LocalDate.of(2025, 6, 5), new BigDecimal("25000.00"), new BigDecimal("162.0000"), new BigDecimal("154.3210"), null, "COMPLETED"));
            transactions.save(new Transaction(null, 1L, "SIP", LocalDate.of(2026, 7, 5), new BigDecimal("30000.00"), new BigDecimal("181.5000"), new BigDecimal("165.2893"), null, "COMPLETED"));
            transactions.save(new Transaction(null, 2L, "PURCHASE", LocalDate.of(2025, 3, 20), new BigDecimal("97080.00"), new BigDecimal("121.3500"), new BigDecimal("800.0000"), null, "COMPLETED"));
            transactions.save(new Transaction(null, 3L, "PURCHASE", LocalDate.of(2025, 11, 12), new BigDecimal("77500.00"), new BigDecimal("172.0300"), new BigDecimal("450.5000"), null, "COMPLETED"));
        };
    }
}
