package dev.lampirg.webgraph.consume;

public interface ApiKeyGenerator {
    String generateApiKey();

    default String generateApiKeyFromUsername(String username) {
        return generateApiKey();
    }
}
