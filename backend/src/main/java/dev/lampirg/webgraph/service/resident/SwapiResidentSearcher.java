package dev.lampirg.webgraph.service.resident;

import com.fasterxml.jackson.databind.JsonNode;
import dev.lampirg.webgraph.model.Resident;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

@Service
public class SwapiResidentSearcher implements ResidentSearcher {

    private final WebClient webClient;

    private final GraphQlRequestResourcesHandler resourcesHandler;

    public SwapiResidentSearcher(WebClient webClient,
                                 @Value("https://swapi-graphql.netlify.app/.netlify/functions/index") String serviceUrl,
                                 GraphQlRequestResourcesHandler resourcesHandler) {
        this.webClient = webClient.mutate().baseUrl(serviceUrl).build();
        this.resourcesHandler = resourcesHandler;
    }

    @Override
    @SneakyThrows
    public Flux<Resident> findAll() {
        GraphQlClient client = HttpGraphQlClient.builder(webClient).build();
        return client.document(resourcesHandler.getAllPeopleRequest().getContentAsString(StandardCharsets.UTF_8))
                .retrieve("allPeople.people")
                .toEntityList(JsonNode.class)
                .flatMapMany(Flux::fromIterable)
                .map(jsonNode -> new Resident(jsonNode.get("name").asText()));
    }

    @Override
    @SneakyThrows
    public Flux<Resident> findResidentsFromSamePlanet(String name) {
        GraphQlClient client = HttpGraphQlClient.builder(webClient).build();
        return client.document(resourcesHandler.getSamePlanetRequest().getContentAsString(StandardCharsets.UTF_8))
                .retrieve("allPeople.people")
                .toEntityList(JsonNode.class)
                .flatMapMany(Flux::fromIterable)
                .filter(jsonNode -> jsonNode.get("name").asText().equals(name))
                .single()
                .map(jsonNode ->
                        jsonNode.get("homeworld").get("residentConnection").get("residents")
                )
                .flatMapMany(this::neighbourJsonToFlux)
                .filter(Predicate.not(resident -> resident.name().equals(name)));
    }

    private Flux<Resident> neighbourJsonToFlux(JsonNode root) {
        Flux<Resident> flux = Flux.empty();
        for (int i = 0; i < root.size(); i++) {
            flux = flux.concatWith(Mono.just(
                    root.get(i).get("name").asText()
            ).map(Resident::new));
        }
        return flux;
    }
}
