package dev.lampirg.webgraph.service.apikey.generator;

import reactor.core.publisher.Mono;

public interface ReactiveApiKeyGenerator {
    Mono<String> generateApiKey();

    default Mono<String> generateApiKeyFromUsername(String username) {
        return generateApiKey();
    }
}
