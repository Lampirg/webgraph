package dev.lampirg.webgraph.integration;

import dev.lampirg.webgraph.model.Resident;
import dev.lampirg.webgraph.service.resident.SwapiResidentSearcher;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Integration test SwapiResidentSearcher")
@Tag("integration")
class SwapiResidentSearcherIntegrationTests {

    @Autowired
    private SwapiResidentSearcher residentSearcher;

    @Test
    @DisplayName("Test findAll method")
    void testFindAll() {
        List<Resident> actual = residentSearcher.findAll().collectList().block();
        Assertions.assertThat(actual)
                .isNotEmpty()
                .contains(new Resident("Luke Skywalker"));
    }

    @Test
    @DisplayName("Test Luke Skywalker")
    @SneakyThrows
    void givenLuke() {
        List<String> actual = residentSearcher.findResidentsFromSamePlanet("Luke Skywalker")
                .map(Resident::name)
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
                .map(Resident::name)
                .collectList();
        Assertions.assertThatThrownBy(actual::block).isInstanceOf(NoSuchElementException.class);
    }
}
