package dev.lampirg.webgraph.unit;

import dev.lampirg.webgraph.db.ApiHolderRepository;
import dev.lampirg.webgraph.service.apikey.generator.SimpleReactiveApiKeyGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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
@Tag("unit")
class ReactiveApiKeyGeneratorTests {

    @Mock
    private RandomGenerator randomGenerator;
    @InjectMocks
    private SimpleReactiveApiKeyGenerator apiKeyGenerator;

    @Test
    void given30Chars() {
        String key = "ThisForSureShouldBe30CharsLong";
        Mockito.when(randomGenerator.ints('0', 'z' + 1))
                .thenReturn(key.chars());
        String actual = apiKeyGenerator.generateApiKey().block();
        assertThat(actual).isEqualTo(key);
    }

    @Test
    void given30CharsWithRubbish() {
        String generated = "This___For___Sure___Should___Be___30___Chars___Long";
        String key = generated.replaceAll("_", "");
        Mockito.when(randomGenerator.ints('0', 'z' + 1))
                .thenReturn(generated.chars());
        String actual = apiKeyGenerator.generateApiKey().block();
        assertThat(actual).isEqualTo(key);
    }

    @Test
    void given38Chars() {
        String generated = "ThisForSureShouldBe30CharsLongAndFive";
        String key = generated.substring(0, 30);
        Mockito.when(randomGenerator.ints('0', 'z' + 1))
                .thenReturn(generated.chars());
        String actual = apiKeyGenerator.generateApiKey().block();
        assertThat(actual).hasSize(30).isEqualTo(key);
    }
}
