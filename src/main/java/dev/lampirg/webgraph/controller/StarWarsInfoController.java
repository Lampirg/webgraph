package dev.lampirg.webgraph.controller;

import dev.lampirg.webgraph.consume.ResidentSearcher;
import dev.lampirg.webgraph.model.Resident;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/info")
@RequiredArgsConstructor
public class StarWarsInfoController {

    private final ResidentSearcher residentSearcher;

    @GetMapping("/same-residents")
    @ResponseBody
    public Mono<Residents> findResidentsFromSamePlanet(@RequestParam String name) {
        return residentSearcher.findResidentsFromSamePlanet(name)
                .map(Resident::new)
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

    private record Residents(List<Resident> data) {}

}
