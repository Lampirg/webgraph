package dev.lampirg.webgraph;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.random.RandomGenerator;

@Configuration
public class RandomGeneratorProvider {
    @Bean
    public RandomGenerator randomGenerator() {
        return RandomGenerator.getDefault();
    }
}
