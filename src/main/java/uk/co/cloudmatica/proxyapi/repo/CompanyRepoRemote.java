package uk.co.cloudmatica.proxyapi.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.cloudmatica.proxyapi.dto.CompanyDto;
import uk.co.cloudmatica.proxyapi.repo.model.OfficeWrapper;
import uk.co.cloudmatica.proxyapi.repo.model.Officer;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
public class CompanyRepoRemote {

    private static final String COMPANY_RESOURCE = "/Search?";
    private static final String OFFICER_RESOURCE = "/Officers?";
    private static final String CUSTOMER_SEARCH = "CompanyNumber=";
    private static final String QUERY = "Query=";
    private final String url;
    private final WebClient webClient;

    public CompanyRepoRemote(final String url, WebClient webClient) {

        this.url = url;
        this.webClient = webClient;
    }

    public Mono<CompanyDto> findCompanies(final Mono<String> query) {

        return query.transform(this::buildCompUrl).transform(this::getCompany);
    }

    public Mono<List<Officer>> findOfficers(final Mono<String> query) {

        return query.flatMap(f -> this.buildOfficerUrl(query).transform(this::getOffices))
            .map(OfficeWrapper::getOfficers);
    }

    private Mono<String> buildCompUrl(final Mono<String> query) {

        return query.flatMap(companyId -> Mono.just(url.concat(COMPANY_RESOURCE)
            .concat(QUERY).concat(companyId)));
    }

    private Mono<String> buildOfficerUrl(final Mono<String> query) {

        return query.flatMap(companyNumber -> Mono.just(
            url.concat(OFFICER_RESOURCE).concat(CUSTOMER_SEARCH).concat(companyNumber)));
    }

    private Mono<OfficeWrapper> getOffices(final Mono<String> urlMono) {

        return urlMono.flatMap(url -> webClient
            .get()
            .uri(url)
            .accept(APPLICATION_JSON)
            .exchangeToMono(e -> e.bodyToMono(new ParameterizedTypeReference<>() {})));
    }

    private Mono<CompanyDto> getCompany(final Mono<String> urlMono) {

        return urlMono.flatMap(url -> webClient
            .get()
            .uri(url)
            .accept(APPLICATION_JSON)
            .exchangeToMono(e -> e.bodyToMono(new ParameterizedTypeReference<>() {})));
    }
}
