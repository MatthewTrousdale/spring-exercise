package uk.co.cloudmatica.proxyapi.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import uk.co.cloudmatica.proxyapi.repo.model.Company;

public interface CompanyRepo extends ReactiveMongoRepository<Company, String> {

    Mono<Company> findByCompanyNumber(final Mono<String> companyNumber);
}
