package dev.lampirg.webgraph.integration;

import dev.lampirg.webgraph.consume.ResidentSearcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.NoSuchElementException;

@SpringBootTest
class StarWarsInfoControllerSecurityTests {

    private WebTestClient testClient;

    @MockBean
    private ResidentSearcher residentSearcher;

    @BeforeEach
    void setUp(ApplicationContext context) {
        testClient = WebTestClient.bindToApplicationContext(context).build();
    }

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
                .header("Key", "aba")
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
                .header("Key", "aba")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void givenNoAuthorization() {
        testClient.get()
                .uri("/info/same-residents?name=Luke+Skywalker")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void givenWrongAuthorization() {
        testClient.get()
                .uri("/info/same-residents?name=Luke+Skywalker")
                .header("Key", "daba")
                .exchange()
                .expectStatus().isForbidden();
    }
}
