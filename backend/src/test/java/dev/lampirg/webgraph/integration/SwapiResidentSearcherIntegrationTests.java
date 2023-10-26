package dev.lampirg.webgraph.integration;

import dev.lampirg.webgraph.service.resident.SwapiResidentSearcher;
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
@DisplayName("Integration test SwapiResidentSearcher")
class SwapiResidentSearcherIntegrationTests {

    @Autowired
    private SwapiResidentSearcher residentSearcher;

    @Test
    @DisplayName("Test Luke Skywalker")
    @SneakyThrows
    void givenLuke() {
        List<String> actual = residentSearcher.findResidentsFromSamePlanet("Luke Skywalker")
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
                .findResidentsFromSamePlanet("I made that up")
                .collectList();
        Assertions.assertThatThrownBy(actual::block).isInstanceOf(NoSuchElementException.class);
    }
}
