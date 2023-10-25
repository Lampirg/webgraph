package dev.lampirg.webgraph.db;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReactiveMongoApiHolderRepository implements ApiHolderRepository {

    private final MongoEntityInformation<ApiHolder, String> entityInformation;
    private final ReactiveMongoTemplate mongoTemplate;
    @Value("username")
    private String idFieldName;
    @Value("apiKey")
    private String apiKeyFieldName;

    @NotNull
    @Override
    public Mono<ApiHolder> findByUsername(@NotNull String username) {
        return mongoTemplate.findById(username, entityInformation.getJavaType(), entityInformation.getCollectionName());
    }

    @NotNull
    @Override
    public Mono<ApiHolder> findByApiKey(@NotNull String apiKey) {
        return mongoTemplate.query(ApiHolder.class)
                .matching(Query.query(
                        Criteria.where("apiKey")
                                .is(apiKey)
                ))
                .one();
    }

    @NotNull
    @Override
    public <S extends ApiHolder> Mono<S> save(@NotNull S entity) {
        return mongoTemplate.save(entity);
    }

    @NotNull
    @Override
    public <S extends ApiHolder> Flux<S> saveAll(@NotNull Iterable<S> entities) {
        return Flux.fromIterable(entities).flatMap(this::save);
    }

    @NotNull
    @Override
    public <S extends ApiHolder> Flux<S> saveAll(@NotNull Publisher<S> entityStream) {
        return Flux.from(entityStream).flatMap(this::save);
    }

    @NotNull
    @Override
    public Mono<ApiHolder> findById(@NotNull String s) {
        return findByUsername(s);
    }

    @NotNull
    @Override
    public Mono<ApiHolder> findById(@NotNull Publisher<String> id) {
        return Mono.from(id).flatMap(this::findByUsername);
    }

    @NotNull
    @Override
    public Mono<Boolean> existsById(@NotNull String s) {
        return mongoTemplate.exists(
                Query.query(
                        Criteria.where(idFieldName).is(s)
                ),
                entityInformation.getJavaType(),
                entityInformation.getCollectionName()
        );
    }

    @NotNull
    @Override
    public Mono<Boolean> existsById(@NotNull Publisher<String> id) {
        return Mono.from(id).flatMap(this::existsById);
    }

    @Override
    public Mono<Boolean> existsByApiKey(@NotNull String apiKey) {
        return mongoTemplate.exists(
                Query.query(
                        Criteria.where(apiKeyFieldName).is(apiKey)
                ),
                entityInformation.getJavaType(),
                entityInformation.getCollectionName()
        );
    }

    @NotNull
    @Override
    public Flux<ApiHolder> findAll() {
        return mongoTemplate.findAll(entityInformation.getJavaType(), entityInformation.getCollectionName());
    }

    @NotNull
    @Override
    public Flux<ApiHolder> findAllById(@NotNull Iterable<String> strings) {
        return mongoTemplate.find(
                Query.query(
                        Criteria.where(idFieldName).in(toCollection(strings))
                ),
                entityInformation.getJavaType(),
                entityInformation.getCollectionName()
        );
    }

    @NotNull
    @Override
    public Flux<ApiHolder> findAllById(@NotNull Publisher<String> idStream) {
        return Flux.from(idStream).buffer().flatMap(this::findAllById);
    }

    @NotNull
    @Override
    public Mono<Long> count() {
        return mongoTemplate.count(new Query(), entityInformation.getCollectionName());
    }

    @NotNull
    @Override
    public Mono<Void> deleteById(@NotNull String s) {
        return mongoTemplate.remove(
                Query.query(
                        Criteria.where(idFieldName).is(s)
                ),
                entityInformation.getJavaType(),
                entityInformation.getCollectionName()
        ).then();
    }

    @NotNull
    @Override
    public Mono<Void> deleteById(@NotNull Publisher<String> id) {
        return Mono.from(id).map(this::deleteById).then();
    }

    @NotNull
    @Override
    public Mono<Void> delete(@NotNull ApiHolder entity) {
        return mongoTemplate.remove(
                Query.query(
                        Criteria.byExample(entity)
                ),
                entityInformation.getJavaType(),
                entityInformation.getCollectionName()
        ).then();
    }

    @NotNull
    @Override
    public Mono<Void> deleteAllById(@NotNull Iterable<? extends String> strings) {
        return mongoTemplate.remove(
                Query.query(
                        Criteria.where(idFieldName).in(toCollection(strings))
                ),
                entityInformation.getJavaType(),
                entityInformation.getCollectionName()
        ).then();
    }

    @NotNull
    @Override
    public Mono<Void> deleteAll(@NotNull Iterable<? extends ApiHolder> entities) {
        return mongoTemplate.remove(
                Query.query(
                        Criteria.where(idFieldName).in(
                                Flux.fromIterable(entities)
                                        .map(ApiHolder::getUsername)
                                        .collectList().blockOptional().orElse(List.of())
                        )
                ),
                entityInformation.getJavaType(),
                entityInformation.getCollectionName()
        ).then();
    }

    @NotNull
    @Override
    public Mono<Void> deleteAll(@NotNull Publisher<? extends ApiHolder> entityStream) {
        return Flux.from(entityStream).map(this::delete).then();
    }

    @NotNull
    @Override
    public Mono<Void> deleteAll() {
        return mongoTemplate.remove(new Query(), entityInformation.getCollectionName()).then();
    }

    @NotNull
    private static <T> Collection<T> toCollection(Iterable<T> strings) {
        return strings instanceof Collection<T> collection ? collection
                : Flux.fromIterable(strings).collectList().blockOptional().orElse(List.of());
    }
}
