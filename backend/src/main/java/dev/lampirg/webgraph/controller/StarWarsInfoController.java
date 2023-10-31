package dev.lampirg.webgraph.controller;

import dev.lampirg.webgraph.config.PagedResidentsJsonDefinition;
import dev.lampirg.webgraph.model.Resident;
import dev.lampirg.webgraph.model.ResidentsPageWithPrevNextRels;
import dev.lampirg.webgraph.service.resident.ResidentSearcher;
import dev.lampirg.webgraph.util.ForbiddenApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/info")
@CrossOrigin
@RequiredArgsConstructor
public class StarWarsInfoController {

    private final ResidentSearcher residentSearcher;

    @Operation(summary = "Find all residents")
    @ApiResponse(responseCode = "200", description = "Residents")
    @ForbiddenApiResponse
    @GetMapping("/all")
    public Mono<Residents> findAll() {
        return residentSearcher.findAll()
                .collectList()
                .map(Residents::new);
    }

    @Operation(summary = "Find all residents")
    @ApiResponse(
            responseCode = "200", description = "Page with residents",
            content = @Content(schema = @Schema(implementation = PagedResidentsJsonDefinition.class))
    )
    @ForbiddenApiResponse
    @GetMapping("/all/paged")
    public Mono<ResidentsPageWithPrevNextRels> findAll(@ParameterObject Pageable pageable, ServerHttpRequest serverHttpRequest) {
        Flux<Resident> all = residentSearcher.findAll();
        return all
                .skip(pageable.getOffset())
                .take(pageable.getPageSize())
                .collectList()
                .zipWith(
                        all.count(),
                        (residents, count) -> new PageImpl<>(residents, pageable, count)
                )
                .map(page -> getPrevNextRels(page, serverHttpRequest));
    }

    @NotNull
    private ResidentsPageWithPrevNextRels getPrevNextRels(PageImpl<Resident> page, ServerHttpRequest serverHttpRequest) {
        String prev = null;
        if (page.hasPrevious()) {
            prev = String.format("%s?page=%s&size=%s", serverHttpRequest.getPath(), page.getNumber() - 1, page.getSize());
        }
        String next = null;
        if (page.hasNext()) {
            next = String.format("%s?page=%s&size=%s", serverHttpRequest.getPath(), page.getNumber() + 1, page.getSize());
        }
        return new ResidentsPageWithPrevNextRels(page, prev, next);
    }

    @Operation(summary = "Find residents from the same planet")
    @ApiResponse(responseCode = "200", description = "Residents")
    @ForbiddenApiResponse
    @ApiResponse(responseCode = "404", description = "Resident not found", content = @Content)
    @GetMapping("/same-residents")
    public Mono<Residents> findResidentsFromSamePlanet(@Parameter(description = "Resident to find from") @RequestParam String name) {
        return residentSearcher.findResidentsFromSamePlanet(name)
                .collectList()
                .map(Residents::new);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public Mono<ErrorResponse> noSuchElement(NoSuchElementException exception) {
        return Mono.just(
                ErrorResponse
                        .builder(exception, HttpStatus.NOT_FOUND, "No such resident")
                        .build()
        );
    }

    private record Residents(List<Resident> data) {
    }


}
