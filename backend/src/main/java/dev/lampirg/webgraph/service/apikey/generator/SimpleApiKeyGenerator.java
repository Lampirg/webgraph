package dev.lampirg.webgraph.service.apikey.generator;

import dev.lampirg.webgraph.db.ApiHolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
public class SimpleApiKeyGenerator implements ApiKeyGenerator {

    private final ApiHolderRepository apiHolderRepository;
    private final RandomGenerator randomGenerator;

    @Override
    @SneakyThrows
    public String generateApiKey() {
        String key;
        do {
            key = randomGenerator.ints('0', 'z' + 1)
                    .filter(i -> Character.isDigit(i) || Character.isAlphabetic(i))
                    .limit(30)
                    .collect(
                            StringBuilder::new,
                            StringBuilder::appendCodePoint,
                            StringBuilder::append
                    ).toString();
        } while (Boolean.TRUE.equals(apiHolderRepository.existsByApiKey(key).block()));
        return key;
    }
}
