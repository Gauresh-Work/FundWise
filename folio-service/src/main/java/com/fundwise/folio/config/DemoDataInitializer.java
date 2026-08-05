package com.fundwise.folio.config;

import com.fundwise.folio.entity.Folio;
import com.fundwise.folio.repository.FolioRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataInitializer {

    @Bean
    CommandLineRunner seedFolios(FolioRepository folios) {
        return args -> {
            if (folios.count() > 0) return;
            folios.save(new Folio(null, 1L, 1L, "FW10024589", "ACTIVE", new BigDecimal("1250.7500"), new BigDecimal("160.4500"), new BigDecimal("233522.53")));
            folios.save(new Folio(null, 1L, 2L, "FW10024590", "ACTIVE", new BigDecimal("800.0000"), new BigDecimal("121.3500"), new BigDecimal("104224.00")));
            folios.save(new Folio(null, 2L, 1L, "FW10024591", "ACTIVE", new BigDecimal("450.5000"), new BigDecimal("172.2000"), new BigDecimal("84126.37")));
        };
    }
}
