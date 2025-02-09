package com.cp.kku.housely.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cp.kku.housely.model.Room;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RoomService {
    private final WebClient webClient;

    public RoomService(WebClient webclient) {
        this.webClient = webclient;
    }

    public Flux<Room> getAllRooms() {
        return webClient.get()
                .uri("/rooms")
                .retrieve()
                .bodyToFlux(Room.class);
    }

    public Mono<Room> getRoomById(Long id) {
        return webClient.get()
                .uri("/rooms/{id}", id)
                .retrieve()
                .bodyToMono(Room.class);
    }

    public Mono<Room> createRoom(Room room){
        return webClient.post()
        .uri("/rooms/add")
        .bodyValue(room)
        .retrieve()
        .onStatus(
            status -> status.is4xxClientError() || status.is5xxServerError(),
            response -> Mono.error(new RuntimeException("Error creating room"))
        )
        .bodyToMono(Room.class);
    }

    public Mono<Room> updateRoom(Long id,Room room){
        return webClient.put()
        .uri("/rooms/update/{id}",id)
        .bodyValue(room)
        .retrieve()
        .onStatus(
            status -> status.is4xxClientError() || status.is5xxServerError(),
            response -> Mono.error(new RuntimeException("Error updating room"))
        )
        .bodyToMono(Room.class);
    }

    public Mono<Void> deleteRoom(Long id){
        return webClient.delete()
        .uri("/rooms/delete/{id}",id)
        .retrieve()
        .bodyToMono(Void.class);
    }

}
