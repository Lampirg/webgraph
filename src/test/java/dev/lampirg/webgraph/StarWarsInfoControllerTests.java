package dev.lampirg.webgraph;

import dev.lampirg.webgraph.consume.ResidentSearcher;
import dev.lampirg.webgraph.controller.StarWarsInfoController;
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
        // TODO: remove excluding ReactiveSecurityAutoConfiguration
        excludeAutoConfiguration = ReactiveSecurityAutoConfiguration.class
)
class StarWarsInfoControllerTests {

    @Autowired
    private WebTestClient testClient;

    @MockBean
    private ResidentSearcher residentSearcher;

    @Test
    void givenExistentName() {
        Mockito.when(residentSearcher.findResidentsFromSamePlanet("Luke Skywalker"))
                .thenReturn(Flux.just("C-3PO", "Darth Vader"));
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
