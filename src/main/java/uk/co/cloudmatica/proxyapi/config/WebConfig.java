package uk.co.cloudmatica.proxyapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.HypermediaWebClientConfigurer;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import uk.co.cloudmatica.proxyapi.handler.ErrorHandler;
import uk.co.cloudmatica.proxyapi.handler.ProxyHandler;
import uk.co.cloudmatica.proxyapi.repo.CompanyRepoRemote;
import uk.co.cloudmatica.proxyapi.service.CompanyService;

import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@Slf4j
@EnableHypermediaSupport(type = HAL)
@Configuration
public class WebConfig {

    private final String proxyEndpoint;
    private final String apiKey;
    private final String apiValue;

    public WebConfig(@Value("${proxy.endpoint}") final String proxyEndpoint,
                     @Value("${proxy.apiKey}") final String apiKey,
                     @Value("${proxy.apiValue}") final String apiValue) {

        this.proxyEndpoint = proxyEndpoint;
        this.apiKey = apiKey;
        this.apiValue = apiValue;
    }

    @Bean
    @ConditionalOnMissingBean
    public WebClient webClient() {

        return WebClient.builder()
            .defaultHeader(apiKey, apiValue)
            .baseUrl(proxyEndpoint)
            .filter(logRequest())
            .build();
    }

    @Bean
    WebClient.Builder webClientBuilder(final HypermediaWebClientConfigurer configurer) {
        return configurer.registerHypermediaTypes(WebClient.builder());
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return just(clientRequest);
        });
    }

    @Bean
    WebClientCustomizer webClientCustomizer(final HypermediaWebClientConfigurer configurer) {
        return configurer::registerHypermediaTypes;
    }

    @Bean
    CompanyRepoRemote companyRepoRemote(final WebClient webClient) {

        return new CompanyRepoRemote(proxyEndpoint, webClient);
    }

    private final static String PROXY = "/proxy";

    @Bean
    RouterFunction<ServerResponse> route(final ProxyHandler proxyHandler) {

        return RouterFunctions
            .route(RequestPredicates.POST(PROXY)
                .and(RequestPredicates
                    .accept(APPLICATION_JSON)), proxyHandler::proxy);
    }

    @Bean
    ProxyHandler proxyHandler(final CompanyService companyService, final ErrorHandler errorHandler) {

        return new ProxyHandler(companyService, errorHandler);
    }

    @Bean
    ErrorHandler errorHandler() {
        return new ErrorHandler();
    }
}