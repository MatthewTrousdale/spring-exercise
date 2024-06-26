package uk.co.cloudmatica.proxyapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.cloudmatica.proxyapi.repo.CompanyRepo;
import uk.co.cloudmatica.proxyapi.repo.CompanyRepoRemote;
import uk.co.cloudmatica.proxyapi.service.CompanyService;

@Configuration
public class AppConfig {

    @Bean
    CompanyService companyService(final CompanyRepoRemote companyRepoRemote, final CompanyRepo companyRepo) {

        return new CompanyService(companyRepoRemote, companyRepo);
    }
}
