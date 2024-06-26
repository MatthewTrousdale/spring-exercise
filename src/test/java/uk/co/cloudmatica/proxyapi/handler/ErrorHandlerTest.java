package uk.co.cloudmatica.proxyapi.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("ErrorHandler Unit Tests")
class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void notFoundTest() {

        ServerRequest serverRequest =
            ServerRequest.create(MockServerWebExchange
                .from(MockServerHttpRequest.post("p").build()),
                HandlerStrategies.withDefaults().messageReaders());

        errorHandler.notFound(serverRequest)
            .subscribe(checkResponse());
    }

    private static Consumer<ServerResponse> checkResponse() {
        return serverResponse -> {
            assertThat(serverResponse.statusCode(), is(HttpStatus.NOT_FOUND));
        };
    }
}
