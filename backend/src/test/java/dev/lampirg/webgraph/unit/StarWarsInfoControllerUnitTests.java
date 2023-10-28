package dev.lampirg.webgraph.unit;

import dev.lampirg.webgraph.model.Resident;
import dev.lampirg.webgraph.service.resident.ResidentSearcher;
import dev.lampirg.webgraph.controller.StarWarsInfoController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.NoSuchElementException;

@WebFluxTest(controllers = StarWarsInfoController.class)
class StarWarsInfoControllerUnitTests {

    @Autowired
    private WebTestClient testClient;

    @MockBean
    private ResidentSearcher residentSearcher;

    @Test
    @WithMockUser
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
                .header("Key", "aba")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().json(expected);
    }

    @Test
    @WithMockUser
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
    @WithAnonymousUser
    void givenNoAuthorization() {
        testClient.get()
                .uri("/info/same-residents?name=Luke+Skywalker")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
