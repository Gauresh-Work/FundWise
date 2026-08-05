package com.fundwise.scheme.config;

import com.fundwise.scheme.entity.NavHistory;
import com.fundwise.scheme.entity.Scheme;
import com.fundwise.scheme.repository.NavHistoryRepository;
import com.fundwise.scheme.repository.SchemeRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataInitializer {

    @Bean
    CommandLineRunner seedSchemes(SchemeRepository schemes, NavHistoryRepository navHistory) {
        return args -> {
            if (schemes.count() > 0) return;

            Scheme equity = new Scheme(null, "FW-EQ-001", "FundWise Bluechip Equity Fund", "EQUITY",
                    new BigDecimal("1.15"), new BigDecimal("186.7400"), "ACTIVE");
            Scheme hybrid = new Scheme(null, "FW-HY-002", "FundWise Balanced Advantage Fund", "HYBRID",
                    new BigDecimal("0.85"), new BigDecimal("130.2800"), "ACTIVE");
            schemes.save(equity);
            schemes.save(hybrid);

            navHistory.save(new NavHistory(null, equity.getId(), java.time.LocalDate.of(2026, 8, 1), new BigDecimal("184.3250")));
            navHistory.save(new NavHistory(null, equity.getId(), java.time.LocalDate.of(2026, 8, 4), new BigDecimal("186.7400")));
            navHistory.save(new NavHistory(null, hybrid.getId(), java.time.LocalDate.of(2026, 8, 1), new BigDecimal("128.9100")));
            navHistory.save(new NavHistory(null, hybrid.getId(), java.time.LocalDate.of(2026, 8, 4), new BigDecimal("130.2800")));
        };
    }
}
