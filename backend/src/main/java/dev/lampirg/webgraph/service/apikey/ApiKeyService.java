package dev.lampirg.webgraph.service.apikey;

import dev.lampirg.webgraph.db.ApiHolder;
import dev.lampirg.webgraph.db.ApiHolderRepository;
import dev.lampirg.webgraph.service.apikey.generator.ReactiveApiKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ReactiveApiKeyGenerator apiKeyGenerator;
    private final ApiHolderRepository apiHolderRepository;

    public Flux<ApiHolder> findAll() {
        return apiHolderRepository.findAll();
    }
    public Mono<ApiHolder> findByApiKey(String apiKey) {
        return apiHolderRepository.findByApiKey(apiKey);
    }

    public Mono<ApiHolder> findByUsername(String username) {
        return apiHolderRepository.findByUsername(username);
    }

    public Mono<Void> save(String username) {
        return apiKeyGenerator.generateApiKey()
                .map(key -> ApiHolder.user(username, key))
                .flatMap(apiHolderRepository::save)
                .then();
    }

    public Mono<Void> save(ApiHolder apiHolder) {
        return apiHolderRepository.save(apiHolder).then();
    }

    public Mono<Boolean> containsKey(String apiKey) {
        return apiHolderRepository.existsByApiKey(apiKey);
    }

    public Mono<Boolean> containsUsername(String username) {
        return apiHolderRepository.existsById(username);
    }

    public Mono<Void> deleteByUsername(String username) {
        return apiHolderRepository.deleteById(username);
    }

}
