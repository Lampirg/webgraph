package dev.lampirg.webgraph;

import dev.lampirg.webgraph.consume.ResidentSearcher;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest
@DisplayName("Integration test ResidentSearcher")
class ResidentSearcherIntegrationTests {

    @Autowired
    private ResidentSearcher residentSearcher;

    @Test
    @DisplayName("Test Luke Skywalker")
    @SneakyThrows
    void givenLuke() {
        List<String> actual = residentSearcher.getLanguage("Luke Skywalker")
                .collectList()
                .block();
        Assertions.assertThat(actual)
                .isNotEmpty()
                .doesNotContain("Luke Skywalker");
    }

    @Test
    @DisplayName("Test nonexistent resident")
    void givenNoResidents() {
        Mono<List<String>> actual = residentSearcher
                .getLanguage("I made that up")
                .collectList();
        Assertions.assertThatThrownBy(actual::block).isInstanceOf(NoSuchElementException.class);
    }
}
