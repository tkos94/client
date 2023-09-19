package com.example.client.service;

import com.example.client.exception.ClientNotFoundException;
import com.example.client.model.Client;
import com.example.client.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Mono<Client> createClient(Client client) {
        validateClientData(client);
        return clientRepository.save(client);
    }

    public Mono<Client> getClientById(String id) {
        return clientRepository.findById(id)
                .switchIfEmpty(Mono.error(new ClientNotFoundException("Cliente não encontrado")));
    }

    public Flux<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Mono<Client> updateClient(String id, Client updatedClient) {
        validateClientData(updatedClient); // Validação personalizada
        return clientRepository.findById(id)
                .switchIfEmpty(Mono.error(new ClientNotFoundException("Cliente não encontrado")))
                .flatMap(existingClient -> {
                    existingClient.setName(updatedClient.getName());
                    existingClient.setAge(updatedClient.getAge());
                    existingClient.setEmail(updatedClient.getEmail());
                    return clientRepository.save(existingClient);
                });
    }

    public Mono<Void> deleteClient(String id) {
        return clientRepository.findById(id)
                .switchIfEmpty(Mono.error(new ClientNotFoundException("Cliente não encontrado")))
                .flatMap(existingClient -> clientRepository.deleteById(existingClient.getId()));
    }

    private void validateClientData(Client client) {
        clientRepository.findByEmail(client.getEmail())
                .filter(existingClient -> !existingClient.getId().equals(client.getId()))
                .flatMap(existingClient -> Mono.error(new IllegalArgumentException("Email já existe")))
                .switchIfEmpty(Mono.just(client));
    }
}
