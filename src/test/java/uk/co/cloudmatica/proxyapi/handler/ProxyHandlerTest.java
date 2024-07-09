package uk.co.cloudmatica.proxyapi.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.test.StepVerifier;
import uk.co.cloudmatica.proxyapi.dto.CompanyDto;
import uk.co.cloudmatica.proxyapi.service.CompanyService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static reactor.core.publisher.Mono.just;
import static reactor.core.publisher.Mono.justOrEmpty;

@DisplayName("ProxyHandler Unit Tests")
@ExtendWith(MockitoExtension.class)
class ProxyHandlerTest {

    @InjectMocks
    private ProxyHandler underTest;

    @Mock
    CompanyService companyServiceMock;
    @Mock
    private ServerRequest serverRequestMock;
    @Mock
    private ErrorHandler errorHandlerMock;

    @Test
    void givenInputTheHandleShouldReturnResponse() {

        final var queryFiledsMono = just(QueryFields.builder().companyNumber("1234").build());
        final var companyMono = just(CompanyDto.builder().build());

        given(serverRequestMock.bodyToMono(QueryFields.class)).willReturn(queryFiledsMono);
        given(companyServiceMock.getCompanyByNumberOrName(any())).willReturn(companyMono);

        StepVerifier
            .create(underTest.proxy(serverRequestMock))
            .assertNext(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
            .verifyComplete();
    }

    @Test
    void givenNoInputTheHandleShouldReturnResponse() {

        final var queryFiledsMono = justOrEmpty(Optional.<QueryFields>empty());
        final var companyMono = just(CompanyDto.builder().build());

        given(serverRequestMock.bodyToMono(QueryFields.class)).willReturn(queryFiledsMono);
        given(companyServiceMock.getCompanyByNumberOrName(any())).willReturn(companyMono);

        StepVerifier
            .create(underTest.proxy(serverRequestMock))
            .assertNext(Assertions::assertNull)
            .assertNext(serverResponse -> notFound());
    }
}