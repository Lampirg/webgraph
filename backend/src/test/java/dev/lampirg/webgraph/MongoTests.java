package dev.lampirg.webgraph;

import dev.lampirg.webgraph.config.EntityInformationProvider;
import dev.lampirg.webgraph.db.ApiHolder;
import dev.lampirg.webgraph.db.ReactiveMongoApiHolderRepository;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@DataMongoTest
@Testcontainers
@Import({ReactiveMongoApiHolderRepository.class, EntityInformationProvider.class})
@Tag("integration")
class MongoTests {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    private ReactiveMongoApiHolderRepository apiHolderRepository;


    @AfterEach
    void tearDown() {
        apiHolderRepository.deleteAll().block();
    }

    @Test
    void save() {
        ApiHolder expected = ApiHolder.user("user", "key");
        apiHolderRepository.save(expected).block();
        ApiHolder actual = apiHolderRepository.findByUsername(expected.getUsername()).block();
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void saveAll() {
        List<ApiHolder> expected = createThreeUsers();
        apiHolderRepository.saveAll(expected).blockLast();
        List<ApiHolder> actual = apiHolderRepository.findAll().collectList().block();
        Assertions.assertThat(actual).hasSameElementsAs(expected);
    }

    @Test
    void findByKey() {
        ApiHolder expected = ApiHolder.user("user", "key");
        apiHolderRepository.save(expected).block();
        ApiHolder actual = apiHolderRepository.findByApiKey(expected.getApiKey()).block();
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAllById() {
        List<ApiHolder> toSave = createThreeUsers();
        apiHolderRepository.saveAll(toSave).blockLast();
        List<String> usernames = List.of("user", "abuser");
        List<ApiHolder> expected = toSave.stream()
                .filter(apiHolder -> usernames.contains(apiHolder.getUsername()))
                .toList();
        List<ApiHolder> actual = apiHolderRepository.findAllById(usernames).collectList().block();
        Assertions.assertThat(actual).hasSameElementsAs(expected);
    }

    @Test
    void existsById() {
        List<ApiHolder> toSave = createThreeUsers();
        apiHolderRepository.saveAll(toSave).blockLast();
        List<String> usernames = List.of("user", "notuser");
        boolean actual = apiHolderRepository.existsById(usernames.get(0)).block();
        Assertions.assertThat(actual).isTrue();
        actual = apiHolderRepository.existsById(usernames.get(1)).block();
        Assertions.assertThat(actual).isFalse();
    }

    @Test
    void existsByApiKey() {
        List<ApiHolder> toSave = createThreeUsers();
        apiHolderRepository.saveAll(toSave).blockLast();
        List<String> keys = List.of("key", "invalid_key");
        boolean actual = apiHolderRepository.existsByApiKey(keys.get(0)).block();
        Assertions.assertThat(actual).isTrue();
        actual = apiHolderRepository.existsById(keys.get(1)).block();
        Assertions.assertThat(actual).isFalse();
    }

    @Test
    void count() {
        List<ApiHolder> toSave = createThreeUsers();
        apiHolderRepository.saveAll(toSave).blockLast();
        long expected = 3;
        long actual = apiHolderRepository.count().block();
        Assertions.assertThat(actual).isEqualTo(expected);
        apiHolderRepository.deleteAll().block();
        expected = 0;
        actual = apiHolderRepository.count().block();
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteById() {
        List<ApiHolder> toSave = createThreeUsers();
        apiHolderRepository.saveAll(toSave).blockLast();
        String username = "user";
        List<ApiHolder> expected = toSave.stream()
                .filter(apiHolder -> !username.equals(apiHolder.getUsername()))
                .toList();
        apiHolderRepository.deleteById(username).block();
        List<ApiHolder> actual = apiHolderRepository.findAll().collectList().block();
        Assertions.assertThat(actual).hasSameElementsAs(expected);
    }

    @Test
    void deleteAllById() {
        List<ApiHolder> toSave = createThreeUsers();
        apiHolderRepository.saveAll(toSave).blockLast();
        List<String> usernames = List.of("user", "abuser");
        List<ApiHolder> expected = toSave.stream()
                .filter(apiHolder -> !usernames.contains(apiHolder.getUsername()))
                .toList();
        apiHolderRepository.deleteAllById(usernames).block();
        List<ApiHolder> actual = apiHolderRepository.findAll().collectList().block();
        Assertions.assertThat(actual).hasSameElementsAs(expected);
    }

    @Test
    void delete() {
        List<ApiHolder> toSave = createThreeUsers();
        apiHolderRepository.saveAll(toSave).blockLast();
        ApiHolder user = toSave.get(0);
        List<ApiHolder> expected = toSave.stream()
                .filter(apiHolder -> !user.equals(apiHolder))
                .toList();
        apiHolderRepository.delete(user).block();
        List<ApiHolder> actual = apiHolderRepository.findAll().collectList().block();
        Assertions.assertThat(actual).hasSameElementsAs(expected);
    }

    @Test
    void deleteAll() {
        List<ApiHolder> toSave = createThreeUsers();
        apiHolderRepository.saveAll(toSave).blockLast();
        List<ApiHolder> users = toSave.subList(0, 2);
        List<ApiHolder> expected = toSave.stream()
                .filter(apiHolder -> !users.contains(apiHolder))
                .toList();
        apiHolderRepository.deleteAll(users).block();
        List<ApiHolder> actual = apiHolderRepository.findAll().collectList().block();
        Assertions.assertThat(actual).hasSameElementsAs(expected);
    }

    @NotNull
    private List<ApiHolder> createThreeUsers() {
        return List.of(
                ApiHolder.user("user", "key"),
                ApiHolder.user("abuser", "kokoko"),
                ApiHolder.user("hello", "world")
        );
    }
}
