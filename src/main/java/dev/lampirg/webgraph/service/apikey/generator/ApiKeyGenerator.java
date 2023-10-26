package dev.lampirg.webgraph.service.apikey.generator;

public interface ApiKeyGenerator {
    String generateApiKey();

    default String generateApiKeyFromUsername(String username) {
        return generateApiKey();
    }
}
