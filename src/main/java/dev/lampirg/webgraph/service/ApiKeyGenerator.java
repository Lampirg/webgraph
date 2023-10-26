package dev.lampirg.webgraph.service;

public interface ApiKeyGenerator {
    String generateApiKey();

    default String generateApiKeyFromUsername(String username) {
        return generateApiKey();
    }
}
