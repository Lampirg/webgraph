package dev.lampirg.webgraph.unit;

import dev.lampirg.webgraph.model.Resident;
import dev.lampirg.webgraph.service.resident.GraphQlRequestResourcesHandler;
import dev.lampirg.webgraph.service.resident.SwapiResidentSearcher;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
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
import java.util.stream.Stream;

@DisplayName("Unit test SwapiResidentSearcher")
class SwapiResidentSearcherUnitTests {

    private SwapiResidentSearcher residentSearcher;

    private MockWebServer mockWebServer;
    private GraphQlRequestResourcesHandler resourcesHandler;
    private Resource smOutput;
    private Resource allOutput;

    public SwapiResidentSearcherUnitTests() {
        mockWebServer = new MockWebServer();
        resourcesHandler = Mockito.mock(GraphQlRequestResourcesHandler.class);
        residentSearcher = new SwapiResidentSearcher(
                WebClient.create(mockWebServer.url("/").toString()),
                mockWebServer.url("/").toString(),
                resourcesHandler
        );
        smOutput = new ClassPathResource("json/sm_output.json");
        allOutput = new ClassPathResource("json/all_output.json");
    }

    @AfterEach
    @SneakyThrows
    void tearDown() {
        mockWebServer.shutdown();
    }

    @SneakyThrows
    private void enqueue(Resource resource) {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.OK.value())
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_GRAPHQL_RESPONSE_VALUE)
                        .setBody(resource.getContentAsString(StandardCharsets.UTF_8)));
    }

    @Nested
    @DisplayName("Test findAll method")
    class TestAllMethod {
        @BeforeEach
        void setUp() {
            enqueue(allOutput);
            Mockito.when(resourcesHandler.getAllPeopleRequest())
                    .thenReturn(new ClassPathResource("json/input.graphql"));
        }

        @Test
        void givenThreeResidents() {
            List<Resident> expected = Stream.of("Luke Skywalker", "C-3PO", "R2-D2")
                    .map(Resident::new).toList();
            List<Resident> actual = residentSearcher.findAll().collectList().block();
            Assertions.assertThat(actual).hasSameElementsAs(expected);
        }
    }

    @Nested
    @DisplayName("Test findResidentsFromSamePlanet method")
    class TestSearchMethod {
        @BeforeEach
        void setUp() {
            enqueue(smOutput);
            Mockito.when(resourcesHandler.getSamePlanetRequest())
                    .thenReturn(new ClassPathResource("json/input.graphql"));
        }

        @Test
        @DisplayName("Test resident with two neighbours")
        @SneakyThrows
        void givenThreeResidents() {
            List<String> expected = List.of("C-3PO", "Darth Vader");
            List<String> actual = residentSearcher.findResidentsFromSamePlanet("Luke Skywalker")
                    .map(Resident::name)
                    .collectList()
                    .block();
            Assertions.assertThat(actual).hasSameElementsAs(expected);
            enqueue(smOutput);
            expected = List.of("Luke Skywalker", "Darth Vader");
            actual = residentSearcher.findResidentsFromSamePlanet("C-3PO")
                    .map(Resident::name)
                    .collectList()
                    .block();
            Assertions.assertThat(actual).hasSameElementsAs(expected);
        }

        @Test
        @DisplayName("Test resident with no neighbours")
        void givenOneResident() {
            List<String> expected = List.of();
            List<String> actual = residentSearcher.findResidentsFromSamePlanet("Kor Ga Gha")
                    .map(Resident::name)
                    .collectList()
                    .block();
            Assertions.assertThat(actual).hasSameElementsAs(expected);
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

}
