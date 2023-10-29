package dev.lampirg.webgraph.unit;

import dev.lampirg.webgraph.controller.StarWarsInfoController;
import dev.lampirg.webgraph.model.Resident;
import dev.lampirg.webgraph.service.resident.ResidentSearcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.NoSuchElementException;

@WebFluxTest(
        controllers = StarWarsInfoController.class,
        excludeAutoConfiguration = ReactiveSecurityAutoConfiguration.class
)
@DisplayName("Unit test StarWarsInfoController")
@Tag("unit")
@Tag("controller")
class StarWarsInfoControllerUnitTests {

    @Autowired
    private WebTestClient testClient;

    @MockBean
    private ResidentSearcher residentSearcher;

    @Test
    @DisplayName("Test findAll method")
    void findAll() {
        Mockito.when(residentSearcher.findAll())
                .thenReturn(Flux.just(new Resident("C-3PO"), new Resident("Darth Vader")));
        String expected = """
                {
                    data: [
                        {"name": "C-3PO"},
                        {"name": "Darth Vader"}
                    ]
                }
                """;
        testClient.get()
                .uri("/info/all")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().json(expected);
    }

    @Nested
    @DisplayName("Test findResidentsFromSamePlanet method")
    class TestFindFromSamePlanet {
        @Test
        void givenExistentName() {
            Mockito.when(residentSearcher.findResidentsFromSamePlanet("Luke Skywalker"))
                    .thenReturn(Flux.just(new Resident("C-3PO"), new Resident("Darth Vader")));
            String expected = """
                    {
                        data: [
                            {"name": "C-3PO"},
                            {"name": "Darth Vader"}
                        ]
                    }
                    """;
            testClient.get()
                    .uri("/info/same-residents?name=Luke+Skywalker")
                    .exchange()
                    .expectStatus().is2xxSuccessful()
                    .expectBody().json(expected);
        }

        @Test
        void givenNonExistentName() {
            Mockito.when(residentSearcher.findResidentsFromSamePlanet("Luke Skywalker"))
                    .thenThrow(new NoSuchElementException());
            testClient.get()
                    .uri("/info/same-residents?name=Luke+Skywalker")
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }
}
