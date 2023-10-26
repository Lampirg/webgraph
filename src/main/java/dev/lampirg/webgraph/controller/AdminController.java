package dev.lampirg.webgraph.controller;

import dev.lampirg.webgraph.service.ApiKeyService;
import dev.lampirg.webgraph.db.ApiHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/key")
@RequiredArgsConstructor
public class AdminController {

    private final ApiKeyService apiKeyService;
    private String notFoundMessage = "%s not found";

    @GetMapping("find-all")
    public Flux<ApiHolder> findAll() {
        return apiKeyService.findAll();
    }

    @GetMapping("find-key")
    public Mono<ApiHolder> findByKey(@RequestParam String apikey) {
        return apiKeyService.findByApiKey(apikey)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(notFoundMessage, apikey))
                ));
    }

    @GetMapping("find-username")
    public Mono<ApiHolder> findByUsername(@RequestParam String username) {
        return apiKeyService.findByUsername(username)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(notFoundMessage, username))
                ));
    }

    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> addUser(@RequestParam String username) {
        return apiKeyService
                .containsUsername(username)
                .flatMap(bool -> Boolean.FALSE.equals(bool) ?
                        apiKeyService.save(username) :
                        Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, username + " already exists"))
                );
    }

    @DeleteMapping("delete")
    public Mono<Void> deleteUser(@RequestParam String username) {
        return apiKeyService
                .containsUsername(username)
                .flatMap(bool -> Boolean.TRUE.equals(bool) ?
                        apiKeyService.deleteByUsername(username) :
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(notFoundMessage, username)))
                );
    }

    private record UsernameAndApiKey(String username, String apiKey) {
    }
}
