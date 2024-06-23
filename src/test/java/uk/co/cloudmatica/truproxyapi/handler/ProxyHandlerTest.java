package uk.co.cloudmatica.truproxyapi.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.test.StepVerifier;
import uk.co.cloudmatica.truproxyapi.dto.CompanyDto;
import uk.co.cloudmatica.truproxyapi.service.ProxyService;

import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;
import static reactor.core.publisher.Mono.justOrEmpty;

@ExtendWith(MockitoExtension.class)
class ProxyHandlerTest {

    private ProxyHandler underTest;

    private static final Supplier<HttpHeaders> HTTP_HEADERS = () -> {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        headers.setAccept(singletonList(APPLICATION_JSON));
        return headers;
    };

    @Mock
    ProxyService proxyServiceMock;
    @Mock
    private ServerRequest serverRequestMock;
    @Mock
    private ServerResponse serverResponseMock;

    @BeforeEach
    void setUp() {
        underTest = new ProxyHandler(proxyServiceMock);
    }

    @Test
    void givenInputTheHandleShouldReturnResponse() {

        final var queryFiledsMono = just(QueryFields.builder()
            .companyNumber("1234").build());
        final var companyMono = just(CompanyDto.builder().build());

        given(serverRequestMock.bodyToMono(QueryFields.class)).willReturn(queryFiledsMono);
        given(proxyServiceMock.getCompany(any())).willReturn(companyMono);

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
        given(proxyServiceMock.getCompany(any())).willReturn(companyMono);

        StepVerifier
            .create(underTest.proxy(serverRequestMock))
            .assertNext(Assertions::assertNull)
            .assertNext(serverResponse -> ServerResponse.notFound());
    }
}