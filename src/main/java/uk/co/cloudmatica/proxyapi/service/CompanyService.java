package uk.co.cloudmatica.proxyapi.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import uk.co.cloudmatica.proxyapi.dto.CompanyDto;
import uk.co.cloudmatica.proxyapi.handler.QueryFields;
import uk.co.cloudmatica.proxyapi.repo.CompanyRepo;
import uk.co.cloudmatica.proxyapi.repo.CompanyRepoRemote;
import uk.co.cloudmatica.proxyapi.repo.model.Company;
import uk.co.cloudmatica.proxyapi.repo.model.Officer;

import java.util.List;
import java.util.function.Function;

import static java.util.List.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static reactor.core.publisher.Mono.just;
import static reactor.core.publisher.Mono.justOrEmpty;

@Slf4j
@AllArgsConstructor
public class CompanyService {

    private final CompanyRepoRemote companyRepoRemote;
    private final CompanyRepo companyRepo;

    public Mono<CompanyDto> getCompanyByNumberOrName(final Mono<QueryFields> params) {

        return params.transform(t -> t.flatMap( q ->
                getCompanyByNumberAndSave(justOrEmpty(q.getCompanyNumber()))
            .switchIfEmpty(t.flatMap(n ->
                getCompanyByName(justOrEmpty(q.getCompanyName()))))));
    }

    private Mono<CompanyDto> getCompanyByNumberAndSave(final Mono<String> query) {

        return query.transform(companyRepo::findByCompanyNumber)
            .map(this::getCompanyDto)
            .switchIfEmpty(findCompanyInRemoteRepoAndAddOfficers(query))
            .map(c -> c.getCompanies().stream().findFirst().orElse(Company.builder().build()))
            .flatMap(companyRepo::save)
            .flatMap(f -> just(this.getCompanyDto(f)));
    }

    private Mono<CompanyDto> getCompanyByName(final Mono<String> query) {

        return query
            .transform(t -> companyRepoRemote.findCompanies(query)
                .transform(this::toCompanyWithOfficers));
    }

    private Mono<CompanyDto> findCompanyInRemoteRepoAndAddOfficers(final Mono<String> query) {

        return companyRepoRemote.findCompanies(query)
            .transform(this::toCompanyWithOfficers);
    }

    private Mono<CompanyDto> toCompanyWithOfficers(final Mono<CompanyDto> companyDto) {

        return companyDto.flatMap(q ->
            companyDto.zipWith(companyRepoRemote.findOfficers(getFirstCompanyOrElseEmpty(q.getCompanies()))
                    .transform(this::filterOfficersWhoHaveResigned))
                .map(getCompanyWithOfficers())
        );
    }

    private Mono<String> getFirstCompanyOrElseEmpty(final List<Company> companies) {

        return justOrEmpty(companies)
            .map(m -> m.getFirst().getCompanyNumber());
    }

    private Mono<List<Officer>> filterOfficersWhoHaveResigned(final Mono<List<Officer>> officerListMono) {

        return officerListMono.map(f -> f.stream()
           .filter(officer -> ofNullable(officer.getResigned_on()).isEmpty())
           .collect(toList()));
    }

    private CompanyDto getCompanyDto(Company company) {
        return CompanyDto
            .builder()
            .totalResults(1)
            .companies(of(company))
            .build();
    }

    private static Function<Tuple2<CompanyDto, List<Officer>>, CompanyDto> getCompanyWithOfficers() {

        return z -> {
            z.getT1().getCompanies().forEach(c -> c.setOfficers(z.getT2()));
            return CompanyDto.builder()
                .totalResults(1)
                .companies(z.getT1().getCompanies()).build();
        };
    }
}
