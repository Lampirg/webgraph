package dev.lampirg.webgraph.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
public class PagingConfiguration implements WebFluxConfigurer {

    @Bean
    public PageRequest defaultPageRequest() {
        return PageRequest.of(0, 20);
    }

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        ReactivePageableHandlerMethodArgumentResolver resolver = new ReactivePageableHandlerMethodArgumentResolver();
        configurer.addCustomResolver(resolver);
    }
}
