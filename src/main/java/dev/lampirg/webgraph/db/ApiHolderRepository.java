package dev.lampirg.webgraph.db;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


public interface ApiHolderRepository extends ReactiveCrudRepository<ApiHolder, String> {
    Mono<ApiHolder> findByUsername(@NotNull String username);
    Mono<ApiHolder> findByApiKey(@NotNull String apiKey);
    Mono<Boolean> existsByApiKey(@NotNull String apiKey);
}
