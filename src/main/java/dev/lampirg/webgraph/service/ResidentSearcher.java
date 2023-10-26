package dev.lampirg.webgraph.service;

import reactor.core.publisher.Flux;

public interface ResidentSearcher {
    Flux<String> findResidentsFromSamePlanet(String name);
}
