package dev.lampirg.webgraph.integration;

import dev.lampirg.webgraph.service.apikey.ApiKeyService;
import dev.lampirg.webgraph.service.resident.ResidentSearcher;
import dev.lampirg.webgraph.db.ApiHolder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@SpringBootTest
@ActiveProfiles("test")
class StarWarsInfoControllerSecurityTests {

    private WebTestClient testClient;

    @MockBean
    private ResidentSearcher residentSearcher;

    @MockBean
    private ApiKeyService apiKeyService;

    @BeforeEach
    void setUp(ApplicationContext context) {
        testClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void givenExistentName() {
        Mockito.when(apiKeyService.findByApiKey("aba")).thenReturn(Mono.just(ApiHolder.user("User", "aba")));
        Mockito.when(residentSearcher.findResidentsFromSamePlanet("Luke Skywalker"))
                .thenReturn(Flux.just("C-3PO", "Darth Vader"));
        String expected = getTypicalExpectedOutput();
        testClient.get()
                .uri("/info/same-residents?name=Luke+Skywalker")
                .header("Key", "aba")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().json(expected);
    }

    @Test
    void givenNonExistentName() {
        Mockito.when(apiKeyService.findByApiKey("aba")).thenReturn(Mono.just(ApiHolder.user("User", "aba")));
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
        Mockito.when(apiKeyService.findByApiKey("daba")).thenReturn(Mono.empty());
        testClient.get()
                .uri("/info/same-residents?name=Luke+Skywalker")
                .header("Key", "daba")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void givenAdmin() {
        Mockito.when(apiKeyService.findByApiKey("aba")).thenReturn(Mono.just(ApiHolder.admin("Admin", "aba")));
        Mockito.when(residentSearcher.findResidentsFromSamePlanet("Luke Skywalker"))
                .thenReturn(Flux.just("C-3PO", "Darth Vader"));
        String expected = getTypicalExpectedOutput();
        testClient.get()
                .uri("/info/same-residents?name=Luke+Skywalker")
                .header("Key", "aba")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().json(expected);
    }

    @NotNull
    private static String getTypicalExpectedOutput() {
        return """
                {
                    data: [
                        {"name": "C-3PO"},
                        {"name": "Darth Vader"}
                    ]
                }
                """;
    }
}
