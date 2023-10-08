package dev.lampirg.webgraph;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientProvider {
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}
