package dev.lampirg.webgraph.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return authentication -> {
            if (!authentication.getCredentials().equals("aba")) {
                authentication.setAuthenticated(false);
                return Mono.error(new AuthenticationCredentialsNotFoundException("No valid credentials."));
            }
            return Mono.just(authentication);
        };
    }

    @Bean
    public ServerAuthenticationConverter serverAuthenticationConverter() {
        return exchange -> {
            Optional<String> key = Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("Key"));
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            "anon",
                            key.orElse(""),
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );
            return Mono.just(authentication);
        };
    }

    @Bean
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
                        .anyExchange().authenticated())
                .addFilterAt(filter(), SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }

}
