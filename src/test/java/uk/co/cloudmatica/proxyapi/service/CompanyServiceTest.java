package uk.co.cloudmatica.proxyapi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import uk.co.cloudmatica.proxyapi.dto.CompanyDto;
import uk.co.cloudmatica.proxyapi.handler.QueryFields;
import uk.co.cloudmatica.proxyapi.repo.CompanyRepo;
import uk.co.cloudmatica.proxyapi.repo.CompanyRepoRemote;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static reactor.core.publisher.Mono.empty;
import static reactor.core.publisher.Mono.just;
import static uk.co.cloudmatica.proxyapi.Fixtures.*;

@DisplayName("CompnyService Unit Tests")
@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @InjectMocks
    private CompanyService underTest;
    @Mock
    private CompanyRepoRemote companyRepoRemoteMock;
    @Mock
    private CompanyRepo companyRepoMock;

    @Test
    void givenCompanyNumberCompanyIsFoundInTheDbNotTheRemoteRepoThenReturnFromDb() {

        final var expectedOfficersList = getOfficersFixture("Badger");

        given(companyRepoMock.findByCompanyNumber(any())).willReturn(just(getCompanyFixture("Mole")));
        given(companyRepoRemoteMock.findCompanies(any())).willReturn(empty());
        given(companyRepoMock.save(any())).willReturn(just(getCompanyFixture("Badger")));

        StepVerifier.create(underTest.getCompanyByNumberOrName(just(QueryFields.builder().companyNumber("123").build())))
            .assertNext(actual -> {
                assertThat(actual.getCompanies()).isNotNull();
                assertThat(actual.getCompanies().size()).isEqualTo(1);
                assertThat(actual.getCompanies().getFirst().getOfficers()).isNotNull();
                final var actualOfficer = actual.getCompanies().getFirst().getOfficers();
                assertThat(actualOfficer).isNotNull();
                assertThat(actualOfficer).usingRecursiveComparison().isEqualTo(expectedOfficersList);
            }).verifyComplete();
    }

    @Test
    void givenCompanyNumberOfUnsavedThenReturnCompanyFromRemoteRepoThenSavedToDb(){

        final var expectedOfficersList = getOfficersFixture("Badger");

        given(companyRepoMock.findByCompanyNumber(any())).willReturn(empty());
        given(companyRepoRemoteMock.findCompanies(any())).willReturn(just(getCompanyDtoFixture("Mole")));
        given(companyRepoRemoteMock.findOfficers(any())).willReturn(just(getOfficersFixture("Mole")));
        given(companyRepoMock.save(any())).willReturn(just(getCompanyFixture("Badger")));

        StepVerifier.create(underTest.getCompanyByNumberOrName(just(QueryFields.builder().companyNumber("123").build())))
            .assertNext(actual -> {
                assertThat(actual).isNotNull();
                assertThat(actual.getCompanies()).isNotNull();
                assertThat(actual.getCompanies().size()).isEqualTo(1);
                assertThat(actual.getCompanies().getFirst().getOfficers()).isNotNull();
                final var actualOfficer = actual.getCompanies().getFirst().getOfficers();
                assertThat(actualOfficer).isNotNull();
                assertThat(actualOfficer).usingRecursiveComparison().isEqualTo(expectedOfficersList);
            }).verifyComplete();
    }

    @Test
    void givenCompanyNameThenTheCompanyComesFromRemoteRepoAndIsNotSaved(){

        final var expectedOfficersList = getOfficersFixture("Badger");

        given(companyRepoMock.findByCompanyNumber(any())).willReturn(empty());
        given(companyRepoRemoteMock.findCompanies(any())).willReturn(just(getCompanyDtoFixture("Mole")));
        given(companyRepoRemoteMock.findOfficers(any())).willReturn(just(getOfficersFixture("Badger")));
        given(companyRepoMock.save(any())).willReturn(empty());

        StepVerifier.create(underTest.getCompanyByNumberOrName(just(QueryFields.builder().companyName("Cloudmatica").build())))
            .assertNext(actual -> {
                assertThat(actual).isNotNull();
                assertThat(actual.getCompanies()).isNotNull();
                assertThat(actual.getCompanies().size()).isEqualTo(1);
                assertThat(actual.getCompanies().getFirst().getOfficers()).isNotNull();
                final var actualOfficer = actual.getCompanies().getFirst().getOfficers();
                assertThat(actualOfficer).isNotNull();
                assertThat(actualOfficer).usingRecursiveComparison().isEqualTo(expectedOfficersList);
            }).verifyComplete();
    }

    @Test
    void givenTheRemoteReturnsNoCompaniesThen(){

        given(companyRepoMock.findByCompanyNumber(any())).willReturn(empty());
        given(companyRepoRemoteMock.findCompanies(any())).willReturn(just(CompanyDto.builder().companies(of())
            .build()));
        given(companyRepoRemoteMock.findOfficers(any())).willReturn(just(getOfficersFixture("Badger")));
        given(companyRepoMock.save(any())).willReturn(empty());

        StepVerifier.create(underTest.getCompanyByNumberOrName(just(QueryFields.builder().companyNumber("123")
                .build())))
            .assertNext(actual -> {
                assertThat(actual).isNotNull();
                assertThat(actual.getCompanies()).isEmpty();
            }).verifyComplete();
    }

    @Test
    void givenCompanyNameAndResignedOfficersThenReturnResultsWithout(){

        final var expectedOfficersList = getOfficersFixture("Badger");

        given(companyRepoMock.findByCompanyNumber(any())).willReturn(empty());
        given(companyRepoRemoteMock.findCompanies(any())).willReturn(just(getCompanyDtoFixture("Mole")));
        given(companyRepoRemoteMock.findOfficers(any())).willReturn(just(getOfficersWithResignedFixture("Badger")));
        given(companyRepoMock.save(any())).willReturn(empty());

        StepVerifier.create(underTest.getCompanyByNumberOrName(just(QueryFields.builder().companyName("Cloudmatica").build())))
            .assertNext(actual -> {
                assertThat(actual).isNotNull();
                assertThat(actual.getCompanies()).isNotNull();
                assertThat(actual.getCompanies().size()).isEqualTo(1);
                assertThat(actual.getCompanies().getFirst().getOfficers()).isNotNull();
                final var actualOfficer = actual.getCompanies().getFirst().getOfficers();
                assertThat(actualOfficer).isNotNull();
                assertThat(actualOfficer).usingRecursiveComparison().isEqualTo(expectedOfficersList);
            }).verifyComplete();
    }

    @Test
    void givenNoDataTheServiceWillHandBackNone() {

        given(companyRepoRemoteMock.findCompanies(any())).willReturn(empty());
        given(companyRepoMock.findByCompanyNumber(any())).willReturn(empty());

        final var actual = underTest.getCompanyByNumberOrName(just(QueryFields.builder().build())).blockOptional();

        assertThat(actual).isEmpty();
    }
}