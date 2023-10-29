package dev.lampirg.webgraph.integration;

import dev.lampirg.webgraph.service.apikey.ApiKeyService;
import dev.lampirg.webgraph.db.ApiHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
@ActiveProfiles("test")
@Tag("integration")
@Tag("security")
@Tag("controller")
class AdminControllerSecurtiyTests {

    private WebTestClient testClient;

    @MockBean
    private ApiKeyService apiHolderRepository;

    @BeforeEach
    void setUp(ApplicationContext context) {
        testClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void findAll() {
        Mockito.when(apiHolderRepository.findByApiKey("Admin_API")).thenReturn(Mono.just(
                ApiHolder.admin("Admin", "Admin_API")
        ));
        Mockito.when(apiHolderRepository.findAll()).thenReturn(Flux.just(
                ApiHolder.user("Username", "Api_Key"),
                ApiHolder.user("Schmusername", "Another_Api_Key")
        ));
        testClient.get()
                .uri("/key/find-all")
                .header("Key", "Admin_API")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2);
    }

    @Test
    void noKey() {
        testClient.get()
                .uri("/key/find-all")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void invalidKey() {
        Mockito.when(apiHolderRepository.findByApiKey("Invalid_API")).thenReturn(Mono.empty());
        testClient.get()
                .uri("/key/find-all")
                .header("Key", "Invalid_API")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void userKey() {
        Mockito.when(apiHolderRepository.findByApiKey("User_API")).thenReturn(Mono.just(
                ApiHolder.user("User", "User_API")
        ));
        testClient.get()
                .uri("/key/find-all")
                .header("Key", "User_API")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void addUser() {
        Mockito.when(apiHolderRepository.findByApiKey("Admin_API")).thenReturn(Mono.just(
                ApiHolder.admin("Admin", "Admin_API")
        ));
        Mockito.when(apiHolderRepository.containsUsername("Username")).thenReturn(Mono.just(false));
        Mockito.when(apiHolderRepository.save("Username")).thenReturn(Mono.empty());
        testClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/key/create")
                        .queryParam("username", "Username")
                        .build())
                .header("Key", "Admin_API")
                .body(Mono.just(ApiHolder.user("Username", "Api_Key")), ApiHolder.class)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void deleteUser() {
        Mockito.when(apiHolderRepository.findByApiKey("Admin_API")).thenReturn(Mono.just(
                ApiHolder.admin("Admin", "Admin_API")
        ));
        Mockito.when(apiHolderRepository.containsUsername("Username")).thenReturn(Mono.just(true));
        Mockito.when(apiHolderRepository.deleteByUsername("Username")).thenReturn(Mono.empty());
        testClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/key/delete")
                        .queryParam("username", "Username")
                        .build())
                .header("Key", "Admin_API")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

}
