package dev.lampirg.webgraph.consume;

import reactor.core.publisher.Flux;

public interface ResidentSearcher {
    Flux<String> findResidentsFromSamePlanet(String name);
}
