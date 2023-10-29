package dev.lampirg.webgraph.service.apikey.generator;

import dev.lampirg.webgraph.db.ApiHolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.random.RandomGenerator;
import java.util.stream.Collector;

@Service
@RequiredArgsConstructor
public class SimpleReactiveApiKeyGenerator implements ReactiveApiKeyGenerator {

    private final ApiHolderRepository apiHolderRepository;
    private final RandomGenerator randomGenerator;

    @Override
    @SneakyThrows
    public Mono<String> generateApiKey() {
        return Flux.fromStream(() -> randomGenerator.ints('0', 'z' + 1).boxed())
                .filter(i -> Character.isDigit(i) || Character.isAlphabetic(i))
                .take(30)
                .collect(Collector.of(
                        StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append
                )).map(StringBuilder::toString)
                .filterWhen(s -> apiHolderRepository.existsByApiKey(s).map(b -> !b))
                .repeatWhenEmpty(Flux::repeat);
    }
}
