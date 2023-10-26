package dev.lampirg.webgraph.security;

import dev.lampirg.webgraph.service.apikey.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApiKeyService apiKeyService;

    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return Mono::just;
    }

    public ServerAuthenticationConverter serverAuthenticationConverter() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("Key"))
                .flatMap(apiKeyService::findByApiKey)
                .map(apiHolder -> UsernamePasswordAuthenticationToken.authenticated(
                        apiHolder.getUsername(), apiHolder.getApiKey(), apiHolder.getAuthorities()
                ))
                .cast(Authentication.class)
                .switchIfEmpty(Mono.just(notGranted()));
    }

    public Authentication notGranted() {
        return UsernamePasswordAuthenticationToken.unauthenticated(
                "not_granted",
                "");
    }


    public AuthenticationWebFilter filter() {
        AuthenticationWebFilter authenticationWebFilter =
                new AuthenticationWebFilter(reactiveAuthenticationManager());
        authenticationWebFilter.setServerAuthenticationConverter(serverAuthenticationConverter());
        return authenticationWebFilter;
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchangeSpec -> exchangeSpec
                        .pathMatchers("/key/**").hasRole("ADMIN")
                        .anyExchange().authenticated())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterAt(filter(), SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }

}
