package dev.lampirg.webgraph.service.resident;

import dev.lampirg.webgraph.model.Resident;
import reactor.core.publisher.Flux;

public interface ResidentSearcher {
    Flux<Resident> findResidentsFromSamePlanet(String name);
}
