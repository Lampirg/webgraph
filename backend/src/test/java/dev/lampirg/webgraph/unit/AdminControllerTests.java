package dev.lampirg.webgraph.unit;

import dev.lampirg.webgraph.controller.AdminController;
import dev.lampirg.webgraph.db.ApiHolder;
import dev.lampirg.webgraph.service.apikey.ApiKeyService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(
        controllers = AdminController.class,
        excludeAutoConfiguration = ReactiveSecurityAutoConfiguration.class
)
@Tag("unit")
@Tag("controller")
class AdminControllerTests {

    @Autowired
    private WebTestClient testClient;

    @MockBean
    ApiKeyService apiKeyService;

    @Test
    void findAll() {
        Mockito.when(apiKeyService.findAll()).thenReturn(Flux.just(
                ApiHolder.user("Username", "Api_Key"),
                ApiHolder.user("Schmusername", "Another_Api_Key")
        ));
        testClient.get()
                .uri("/key/find-all")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2);
    }

    @Test
    void findOne() {
        Mockito.when(apiKeyService.findByUsername("Username")).thenReturn(Mono.just(
                ApiHolder.user("Username", "Api_Key")
        ));
        Mockito.when(apiKeyService.findByApiKey("Api_Key")).thenReturn(Mono.just(
                ApiHolder.user("Username", "Api_Key")
        ));
        testClient.get()
                .uri("/key/find-username?username=Username")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.username").isEqualTo("Username");
        testClient.get()
                .uri("/key/find-key?apikey=Api_Key")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.username").isEqualTo("Username");
    }

    @Test
    void findNoOne() {
        Mockito.when(apiKeyService.findByUsername("Not Username")).thenReturn(Mono.empty());
        testClient.get()
                .uri("/key/find-username?username=Not+Username")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void addUser() {
        Mockito.when(apiKeyService.containsUsername("Username")).thenReturn(Mono.just(false));
        Mockito.when(apiKeyService.save("Username")).thenReturn(Mono.empty());
        testClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/key/create")
                        .queryParam("username", "Username")
                        .build())
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void addExistingUser() {
        Mockito.when(apiKeyService.containsUsername("Username")).thenReturn(Mono.just(true));
        testClient.post()
                .uri("/key/create")
                .body(Mono.just(ApiHolder.user("Username", "Api_Key")), ApiHolder.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void deleteUser() {
        Mockito.when(apiKeyService.containsUsername("Username")).thenReturn(Mono.just(true));
        Mockito.when(apiKeyService.deleteByUsername("Username")).thenReturn(Mono.empty());
        testClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/key/delete")
                        .queryParam("username", "Username")
                        .build())
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void deleteNonExistentUser() {
        Mockito.when(apiKeyService.containsUsername("Username")).thenReturn(Mono.just(false));
        testClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/key/delete")
                        .queryParam("username", "Username")
                        .build())
                .exchange()
                .expectStatus().isNotFound();
    }
}
