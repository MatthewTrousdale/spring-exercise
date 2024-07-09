package uk.co.cloudmatica.proxyapi.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import uk.co.cloudmatica.proxyapi.dto.ErrorResponseDto;
import uk.co.cloudmatica.proxyapi.handler.exceptions.CompanyNotFoundException;

@Slf4j
public class ErrorHandler {

    private static final String NOT_FOUND = "not found";
    private static final String ERROR_RAISED = "error raised";

    public Mono<ServerResponse> notFound(final ServerRequest request) {
        return Mono.just(new CompanyNotFoundException(NOT_FOUND)).transform(this::getResponse);
    }

    Mono<ServerResponse> throwableError(final Throwable error) {
        log.error(ERROR_RAISED, error);
        return Mono.just(error).transform(this::getResponse);
    }

    <T extends Throwable> Mono<ServerResponse> getResponse(final Mono<T> monoError) {
        return monoError.transform(ThrowableTranslator::translate)
            .flatMap(translation -> ServerResponse
                .status(translation.getHttpStatus())
                .body(Mono.just(new ErrorResponseDto(translation.getMessage())), ErrorResponseDto.class));
    }
}

