package dev.lampirg.webgraph.security;

import dev.lampirg.webgraph.service.apikey.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

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
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchangeSpec -> exchangeSpec
                        .pathMatchers("/key/**").hasRole("ADMIN")
                        .pathMatchers("/info/**").hasRole("USER")
                        .anyExchange().permitAll())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
                .addFilterAt(filter(), SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }

}
