package dev.lampirg.webgraph.service.resident;

import com.fasterxml.jackson.databind.JsonNode;
import dev.lampirg.webgraph.model.Resident;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

@Service
public class SwapiResidentSearcher implements ResidentSearcher {

    private final WebClient webClient;

    private Resource resource;

    public SwapiResidentSearcher(WebClient webClient,
                                 @Value("https://swapi-graphql.netlify.app/.netlify/functions/index") String serviceUrl,
                                 @Value("classpath:graphql-documents/sameplanet.graphql") Resource resource) {
        this.webClient = webClient.mutate().baseUrl(serviceUrl).build();
        this.resource = resource;
    }

    @Override
    @SneakyThrows
    public Flux<Resident> findResidentsFromSamePlanet(String name) {
        GraphQlClient client = HttpGraphQlClient.builder(webClient).build();
        return client.document(resource.getContentAsString(StandardCharsets.UTF_8))
                .retrieve("allPeople.people")
                .toEntityList(JsonNode.class)
                .flatMapMany(Flux::fromIterable)
                .filter(jsonNode -> jsonNode.get("name").asText().equals(name))
                .single()
                .map(jsonNode ->
                        jsonNode.get("homeworld").get("residentConnection").get("residents")
                )
                .flatMapMany(this::jsonToFlux)
                .filter(Predicate.not(resident -> resident.name().equals(name)));
    }

    private Flux<Resident> jsonToFlux(JsonNode root) {
        Flux<Resident> flux = Flux.empty();
        for (int i = 0; i < root.size(); i++) {
            flux = flux.concatWith(Flux.just(
                    root.get(i).get("name").asText()
            ).map(Resident::new));
        }
        return flux;
    }
}
