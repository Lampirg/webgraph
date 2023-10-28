package dev.lampirg.webgraph.service.resident;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@Getter
public class GraphQlRequestResourcesHandler {

    private final Resource allPeopleRequest;
    private final Resource samePlanetRequest;

    public GraphQlRequestResourcesHandler(
            @Value("classpath:graphql-documents/all.graphql") Resource allPeopleRequest,
            @Value("classpath:graphql-documents/sameplanet.graphql") Resource samePlanetRequest
    ) {
        this.allPeopleRequest = allPeopleRequest;
        this.samePlanetRequest = samePlanetRequest;
    }
}
