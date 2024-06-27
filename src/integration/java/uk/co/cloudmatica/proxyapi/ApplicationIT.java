package uk.co.cloudmatica.proxyapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import uk.co.cloudmatica.proxyapi.dto.CompanyDto;
import uk.co.cloudmatica.proxyapi.handler.QueryFields;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static reactor.core.publisher.Mono.just;

@DisplayName("Application Integration Tests")
public class ApplicationIT extends IntegrationTestBase {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        webTestClient = webTestClient.mutate()
            .responseTimeout(Duration.ofMillis(40000))
            .build();
    }

    @Test
    void whenIAskForAnExistingCompanyWithCustomerNumberItsReturnedWithOfficersIncluded() {

        webTestClient.post()
            .uri("/proxy")
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromPublisher(just(QueryFields
                .builder()
                .companyName("BBC LIMITED")
                .companyNumber("06500244")
                .build()), QueryFields.class))
            .exchange()
            .expectStatus().isOk()
            .expectBody(CompanyDto.class)
            .value(response -> {
                assertThat(response).isNotNull();
                assertThat(response.getCompanies()).isNotNull();
                assertThat(response.getCompanies().getFirst().getCompanyNumber()).isEqualTo("06500244");
                assertThat(response.getCompanies().getFirst().getOfficers()).isNotNull();
                assertThat(response.getCompanies().getFirst().getOfficers().size()).isEqualTo(2);
            });
    }

    @Test
    void whenIAskForAnExistingCompanyWithCustomerNmeItsReturnedWithOfficersIncluded() {

        webTestClient.post()
            .uri("/proxy")
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromPublisher(just(QueryFields
                .builder()
                .companyName("BBC LIMITED")
                .build()), QueryFields.class))
            .exchange()
            .expectStatus().isOk()
            .expectBody(CompanyDto.class)
            .value(response -> {
                assertThat(response).isNotNull();
                assertThat(response.getCompanies()).isNotNull();
                assertThat(response.getCompanies().getFirst().getCompanyNumber()).isEqualTo("06500244");
                assertThat(response.getCompanies().getFirst().getOfficers()).isNotNull();
                assertThat(response.getCompanies().getFirst().getOfficers().size()).isEqualTo(2);
            });
    }

    @Test
    void whenIAskForAnExistingCompanyWithNonExistingCompany404() {

        webTestClient.post()
            .uri("/proxy")
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromPublisher(just(QueryFields
                .builder()
                .companyName("BAD-NUMBER")
                .build()), QueryFields.class))
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void whenIAskForAnEntityWithoutParamsIs404() {

        webTestClient.post()
            .uri("/proxy")
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromPublisher(just(QueryFields
                .builder()
                .build()), QueryFields.class))
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void whenIAskForAnEntityWithBadParamsIsGracefullyHandled() {

        webTestClient.post()
            .uri("/proxy")
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromPublisher(
                just(new BadRecord("Name", "Face")), BadRecord.class)
            )
            .exchange()
            .expectStatus().isNotFound();
    }

    private record BadRecord (String anyOldName, String anyOldFace) {}
}
