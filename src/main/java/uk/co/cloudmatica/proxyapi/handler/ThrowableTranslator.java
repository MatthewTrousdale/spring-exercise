package uk.co.cloudmatica.proxyapi.handler;

import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import uk.co.cloudmatica.proxyapi.handler.exceptions.CompanyNotFoundException;

import static org.springframework.http.HttpStatus.*;

class ThrowableTranslator {

    private final HttpStatus httpStatus;
    private final String message;

    private ThrowableTranslator(final Throwable throwable) {
        this.httpStatus = getStatus(throwable);
        this.message = throwable.getMessage();
    }

    private HttpStatus getStatus(final Throwable error) {

        return switch (error) {
            case CompanyNotFoundException pnfe  -> NOT_FOUND;
            default                             -> INTERNAL_SERVER_ERROR;
        };
    }

    HttpStatus getHttpStatus() {
        return httpStatus;
    }

    String getMessage() {
        return message;
    }

    static <T extends Throwable> Mono<ThrowableTranslator> translate(final Mono<T> throwable) {
        return throwable.flatMap(error -> Mono.just(new ThrowableTranslator(error)));
    }
}
