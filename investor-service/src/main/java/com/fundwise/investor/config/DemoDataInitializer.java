package com.fundwise.investor.config;

import com.fundwise.investor.entity.BankMandate;
import com.fundwise.investor.entity.Investor;
import com.fundwise.investor.entity.KycDocument;
import com.fundwise.investor.entity.Nominee;
import com.fundwise.investor.repository.BankMandateRepository;
import com.fundwise.investor.repository.InvestorRepository;
import com.fundwise.investor.repository.KycDocumentRepository;
import com.fundwise.investor.repository.NomineeRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataInitializer {

    @Bean
    CommandLineRunner seedInvestors(InvestorRepository investors, BankMandateRepository mandates,
                                    NomineeRepository nominees, KycDocumentRepository documents) {
        return args -> {
            if (investors.count() > 0) return;

            Investor ananya = new Investor(null, "Ananya Sharma", "ananya.sharma@example.com", "9876543210", "ABCDE1234F", "ACTIVE");
            Investor rohan = new Investor(null, "Rohan Mehta", "rohan.mehta@example.com", "9123456780", "FGHIJ5678K", "ACTIVE");
            investors.save(ananya);
            investors.save(rohan);

            mandates.save(new BankMandate(null, ananya.getId(), "HDFC Bank", "XXXX4582", "HDFC0001234", "SAVINGS"));
            mandates.save(new BankMandate(null, rohan.getId(), "ICICI Bank", "XXXX9021", "ICIC0005678", "SAVINGS"));
            nominees.save(new Nominee(null, ananya.getId(), "Aarav Sharma", "Son", new BigDecimal("100.00")));
            nominees.save(new Nominee(null, rohan.getId(), "Kavya Mehta", "Spouse", new BigDecimal("100.00")));
            documents.save(new KycDocument(null, ananya.getId(), "PAN", "ABCDE1234F", null, "VERIFIED"));
            documents.save(new KycDocument(null, rohan.getId(), "PAN", "FGHIJ5678K", null, "VERIFIED"));
        };
    }
}
