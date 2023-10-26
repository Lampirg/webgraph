package dev.lampirg.webgraph.service.apikey;

import dev.lampirg.webgraph.db.ApiHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Mono;

// TODO: replace hardcoded admin

@Configuration
@Profile("hardcode")
@RequiredArgsConstructor
public class HardcodedApiKeys {

    private final ApiKeyService apiKeyService;

    @Bean
    public CommandLineRunner createAdmin() {
        return args -> {
            String apiKey = "ADMINHRDC";
            apiKeyService.containsKey(apiKey)
                    .flatMap(aBoolean -> Boolean.FALSE.equals(aBoolean) ?
                            apiKeyService.save(ApiHolder.admin("Hardcoded Admin User", apiKey)) :
                            Mono.empty()
                    )
                    .block();
        };
    }

    @Bean
    public CommandLineRunner createUser() {
        return args -> {
            String apiKey = "USERHRDC";
            apiKeyService.containsKey(apiKey)
                    .flatMap(aBoolean -> Boolean.FALSE.equals(aBoolean) ?
                            apiKeyService.save(ApiHolder.user("Hardcoded User", apiKey)) :
                            Mono.empty()
                    )
                    .block();
        };
    }

}
