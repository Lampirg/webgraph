package dev.lampirg.webgraph.model;

import org.springframework.data.domain.Page;

public record ResidentsPageWithPrevNextRels(Page<Resident> page, String prev, String next) {
}
