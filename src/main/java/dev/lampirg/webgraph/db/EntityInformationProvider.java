package dev.lampirg.webgraph.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;
import org.springframework.data.util.TypeInformation;

@Configuration
public class EntityInformationProvider {

    @Bean
    public MongoEntityInformation<ApiHolder, String> mongoEntityInformation(MongoPersistentEntity<ApiHolder> entity) {
        return new MappingMongoEntityInformation<>(entity, String.class);
    }

    @Bean
    public MongoPersistentEntity<ApiHolder> mongoPersistentEntity() {
        return new BasicMongoPersistentEntity<>(TypeInformation.of(ApiHolder.class));
    }
}
