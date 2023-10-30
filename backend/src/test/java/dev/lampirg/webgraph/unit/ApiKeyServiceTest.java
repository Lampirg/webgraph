package dev.lampirg.webgraph.unit;

import dev.lampirg.webgraph.db.ApiHolder;
import dev.lampirg.webgraph.db.ApiHolderRepository;
import dev.lampirg.webgraph.service.apikey.ApiKeyService;
import dev.lampirg.webgraph.service.apikey.generator.ReactiveApiKeyGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private ReactiveApiKeyGenerator apiKeyGenerator;
    @Mock
    private ApiHolderRepository apiHolderRepository;
    @InjectMocks
    private ApiKeyService apiKeyService;

    @Test
    void saveGivenUniqueKey() {
        String key = "ThisForSureShouldBe30CharsLong";
        Mockito.when(apiKeyGenerator.generateApiKey())
                .thenReturn(Mono.just(key));
        Mockito.when(apiHolderRepository.existsByApiKey(key))
                .thenReturn(Mono.just(false));
        ApiHolder user = ApiHolder.user("aboba", key);
        Mockito.when(apiHolderRepository.save(user)).thenReturn(Mono.empty());
        Assertions.assertThatNoException().isThrownBy(() -> apiKeyService.save("aboba").block());
    }

    @Test
    void saveGivenNonUniqueKey() {
        String nonUnique = "ThisIsNonUniqueAndStill30Chars";
        String unique = "ThisForSureShouldBeAUniqueWord";
        Mockito.when(apiKeyGenerator.generateApiKey())
                .thenReturn(Mono.just(nonUnique))
                .thenReturn(Mono.just(unique));
        Mockito.when(apiHolderRepository.existsByApiKey(nonUnique))
                .thenReturn(Mono.just(true));
        Mockito.when(apiHolderRepository.existsByApiKey(unique))
                .thenReturn(Mono.just(false));
        ApiHolder user = ApiHolder.user("aboba", unique);
        Mockito.when(apiHolderRepository.save(user)).thenReturn(Mono.empty());
        Assertions.assertThatNoException().isThrownBy(() -> apiKeyService.save("aboba").block());
    }
}
