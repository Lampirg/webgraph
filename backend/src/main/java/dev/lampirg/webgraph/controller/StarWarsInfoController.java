package dev.lampirg.webgraph.controller;

import dev.lampirg.webgraph.model.Resident;
import dev.lampirg.webgraph.service.resident.ResidentSearcher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/info")
@CrossOrigin
@RequiredArgsConstructor
public class StarWarsInfoController {

    private final ResidentSearcher residentSearcher;

    @Operation(summary = "Find residents from the same planet")
    @ApiResponse(responseCode = "200", description = "Residents")
    @ApiResponse(responseCode = "403", description = "No valid api key", content = @Content)
    @GetMapping("/all")
    public Mono<Residents> findAll() {
        return residentSearcher.findAll()
                .collectList()
                .map(Residents::new);
    }

    @Operation(summary = "Find residents from the same planet")
    @ApiResponse(responseCode = "200", description = "Residents")
    @ApiResponse(responseCode = "403", description = "No valid api key", content = @Content)
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
