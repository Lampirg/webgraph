package dev.lampirg.webgraph.json;

import dev.lampirg.webgraph.model.Resident;
import dev.lampirg.webgraph.model.ResidentsPageWithPrevNextRels;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class TestPageSerialization {

    @Autowired
    JacksonTester<ResidentsPageWithPrevNextRels> jacksonTester;

    @Test
    @SneakyThrows
    void givenOnePage() {
        List<Resident> residents = Stream.of("Luke Skywalker", "R2-D2")
                .map(Resident::new).toList();
        Page<Resident> page = new PageImpl<>(residents, PageRequest.of(0, 2), 2);
        ResidentsPageWithPrevNextRels response = new ResidentsPageWithPrevNextRels(page, null, null);
        JsonContent<ResidentsPageWithPrevNextRels> result = jacksonTester.write(response);
        assertThat(result).extractingJsonPathArrayValue("$.data").hasSameSizeAs(residents);
        assertThat(result).isStrictlyEqualToJson(new ClassPathResource("serialize/output.json"));
    }

    @Test
    @SneakyThrows
    void givenThreePages() {
        List<Resident> residents = Stream.of("Luke Skywalker", "R2-D2")
                .map(Resident::new).toList();
        Page<Resident> page = new PageImpl<>(residents, PageRequest.of(1, 2), 6);
        ResidentsPageWithPrevNextRels response = new ResidentsPageWithPrevNextRels(page, "prevUrl", "nextUrl");
        JsonContent<ResidentsPageWithPrevNextRels> result = jacksonTester.write(response);
        assertThat(result).extractingJsonPathArrayValue("$.data").hasSameSizeAs(residents);
        assertThat(result).extractingJsonPathStringValue("$.page.next").isEqualTo("nextUrl");
        assertThat(result).extractingJsonPathStringValue("$.page.prev").isEqualTo("prevUrl");
    }

    @Test
    @SneakyThrows
    void givenLastPage() {
        List<Resident> residents = Stream.of("Luke Skywalker", "R2-D2")
                .map(Resident::new).toList();
        Page<Resident> page = new PageImpl<>(residents, PageRequest.of(2, 2), 6);
        ResidentsPageWithPrevNextRels response = new ResidentsPageWithPrevNextRels(page, "prevUrl", null);
        JsonContent<ResidentsPageWithPrevNextRels> result = jacksonTester.write(response);
        assertThat(result).extractingJsonPathArrayValue("$.data").hasSameSizeAs(residents);
        assertThat(result).extractingJsonPathBooleanValue("$.page.hasNext").isEqualTo(false);
        assertThat(result).extractingJsonPathBooleanValue("$.page.hasPrev").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.page.next").isNull();
        assertThat(result).extractingJsonPathStringValue("$.page.prev").isEqualTo("prevUrl");
    }
}
