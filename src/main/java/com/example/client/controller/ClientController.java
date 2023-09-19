package com.example.client.controller;

import com.example.client.exception.ClientNotFoundException;
import com.example.client.model.Client;
import com.example.client.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public Mono<Client> createClient(@RequestBody @Validated Client client) {
        return clientService.createClient(client);
    }

    @GetMapping("/{id}")
    public Mono<Client> getClientById(@PathVariable String id) {
        return clientService.getClientById(id)
                .switchIfEmpty(Mono.error(new ClientNotFoundException("Cliente não encontrado")));
    }

    @GetMapping
    public Flux<Client> getAllClients() {
        return clientService.getAllClients();
    }

    @PutMapping("/{id}")
    public Mono<Client> updateClient(@PathVariable String id, @RequestBody @Validated Client updatedClient) {
        return clientService.updateClient(id, updatedClient);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteClient(@PathVariable String id) {
        return clientService.deleteClient(id);
    }
}
