package uk.co.cloudmatica.proxyapi.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import uk.co.cloudmatica.proxyapi.dto.CompanyDto;
import uk.co.cloudmatica.proxyapi.service.CompanyService;

import static reactor.core.publisher.Mono.just;

@RequiredArgsConstructor
public class ProxyHandler {

    final CompanyService companyService;
    final ErrorHandler errorHandler;

    public Mono<ServerResponse> proxy(final ServerRequest serverRequest) {

        return serverRequest
            .bodyToMono(QueryFields.class).cache()
            .onErrorResume(throwable -> just(QueryFields.builder().build()))
            .transform(this::buildResponse)
            .onErrorResume(errorHandler::throwableError)
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    private Mono<ServerResponse> buildResponse(final Mono<QueryFields> queryFields) {
        return queryFields
            .transform(companyService::getCompanyByNumberOrName)
            .transform(this::serverResponse);
    }

    private Mono<ServerResponse> serverResponse(final Mono<CompanyDto> companyDtoMono) {
        return companyDtoMono.flatMap(appResponse ->
            ServerResponse.ok().body(just(appResponse), CompanyDto.class));
    }
}
