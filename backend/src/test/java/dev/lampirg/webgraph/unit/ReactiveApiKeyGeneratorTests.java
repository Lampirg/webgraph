package dev.lampirg.webgraph.unit;

import dev.lampirg.webgraph.db.ApiHolderRepository;
import dev.lampirg.webgraph.service.apikey.generator.SimpleReactiveApiKeyGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.random.RandomGenerator;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Unit test SimpleApiKeyGenerator")
@ExtendWith(MockitoExtension.class)
class ReactiveApiKeyGeneratorTests {

    @Mock
    private ApiHolderRepository apiHolderRepository;
    @Mock
    private RandomGenerator randomGenerator;
    @InjectMocks
    private SimpleReactiveApiKeyGenerator apiKeyGenerator;

    @Test
    void givenUniqueString() {
        String key = "ThisForSureShouldBe30CharsLong";
        Mockito.when(randomGenerator.ints('0', 'z' + 1))
                .thenReturn(key.chars());
        Mockito.when(apiHolderRepository.existsByApiKey(key))
                .thenReturn(Mono.just(false));
        String actual = apiKeyGenerator.generateApiKey().block();
        assertThat(actual).isEqualTo(key);
    }

    @Test
    void givenRubbish() {
        String generated = "This___For___Sure___Should___Be___30___Chars___Long";
        String key = generated.replaceAll("_", "");
        Mockito.when(randomGenerator.ints('0', 'z' + 1))
                .thenReturn(generated.chars());
        Mockito.when(apiHolderRepository.existsByApiKey(key))
                .thenReturn(Mono.just(false));
        String actual = apiKeyGenerator.generateApiKey().block();
        assertThat(actual).isEqualTo(key);
    }

    @Test
    void givenFirstNonUnique() {
        String nonUnique = "ThisIsNonUniqueAndStill30Chars";
        String unique = "ThisForSureShouldBeAUniqueWord";
        Mockito.when(randomGenerator.ints('0', 'z' + 1))
                .thenReturn(nonUnique.chars(), unique.chars());
        Mockito.when(apiHolderRepository.existsByApiKey(nonUnique))
                .thenReturn(Mono.just(true));
        Mockito.when(apiHolderRepository.existsByApiKey(unique))
                .thenReturn(Mono.just(false));
        String actual = apiKeyGenerator.generateApiKey().block();
        assertThat(actual).isEqualTo(unique);
    }
}
