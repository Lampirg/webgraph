package dev.lampirg.webgraph.config;

import dev.lampirg.webgraph.model.Resident;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PagedResidentsJsonDefinition {

    private final Resident[] data;
    private final Page page;

    @RequiredArgsConstructor
    @Getter
    public static class Page {
        private final int current;
        private final int total;
        private final boolean hasNext;
        private final boolean hasPrev;
        private final String next;
        private final String prev;
    }
}
