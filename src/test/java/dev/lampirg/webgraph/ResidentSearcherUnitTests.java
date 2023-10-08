package dev.lampirg.webgraph;

import dev.lampirg.webgraph.consume.ResidentSearcher;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

@DisplayName("Unit test ResidentSearcher")
class ResidentSearcherUnitTests {

    private ResidentSearcher residentSearcher;

    private MockWebServer mockWebServer;

    private Resource response;

    public ResidentSearcherUnitTests() {
        mockWebServer = new MockWebServer();
        residentSearcher = new ResidentSearcher(
                WebClient.create(mockWebServer.url("/").toString()),
                mockWebServer.url("/").toString(),
                new ClassPathResource("json/input.graphql")
        );
        response = new ClassPathResource("json/output.json");
    }

    @BeforeEach
    @SneakyThrows
    void setUp() {
        enqueue();
    }

    @SneakyThrows
    private void enqueue() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.OK.value())
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_GRAPHQL_RESPONSE_VALUE)
                        .setBody(response.getContentAsString(StandardCharsets.UTF_8)));
    }

    @AfterEach
    @SneakyThrows
    void tearDown() {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("Test resident with two neighbours")
    @SneakyThrows
    void givenThreeResidents() {
        List<String> expected = List.of("C-3PO", "Darth Vader");
        List<String> actual = residentSearcher.getLanguage("Luke Skywalker")
                .collectList()
                .block();
        Assertions.assertThat(actual).hasSameElementsAs(expected);
        enqueue();
        expected = List.of("Luke Skywalker", "Darth Vader");
        actual = residentSearcher.getLanguage("C-3PO")
                .collectList()
                .block();
        Assertions.assertThat(actual).hasSameElementsAs(expected);
    }

    @Test
    @DisplayName("Test resident with no neighbours")
    void givenOneResident() {
        List<String> expected = List.of();
        List<String> actual = residentSearcher.getLanguage("Kor Ga Gha")
                .collectList()
                .block();
        Assertions.assertThat(actual).hasSameElementsAs(expected);
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
