package com.cp.kku.housely.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cp.kku.housely.model.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    private final WebClient webClient;

    public ProductService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Product> getAllProducts() {
        return webClient.get()
                .uri("/products")
                .retrieve()
                .bodyToFlux(Product.class);
    }

    public Mono<Product> getProductById(Long id) {
        return webClient.get()
                .uri("/products/{id}", id)
                .retrieve()
                .bodyToMono(Product.class);
    }

    public Mono<Product> createProduct(Product product) {
        return webClient.post()
                .uri("/products/add")
                .bodyValue(product)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error creating product")))
                .bodyToMono(Product.class);
    }

    public Mono<Product> updateProduct(Long id, Product product) {
        return webClient.put()
                .uri("/products/update/{id}", id)
                .bodyValue(product)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error updating product")))
                .bodyToMono(Product.class);
    }

    public Mono<Void> deleteProduct(Long id){
        return webClient.delete()
        .uri("/products/delete/{id}",id)
        .retrieve()
        .bodyToMono(Void.class);
    }

    public Flux<Product> getProductsByCategoryId(Long categoryId){
        return webClient.get()
        .uri("/products/category/{categoryId}",categoryId)
        .retrieve()
        .bodyToFlux(Product.class);
    }

    public Flux<Product> getProductsByRoomId(Long roomId){
        return webClient.get()
        .uri("/products/room/{roomId}",roomId)
        .retrieve()
        .bodyToFlux(Product.class);
    }

    public Mono<Product> decreaseProductQuantity(Long id, int quantity) {
        return webClient.put()
                .uri("/products/decreaseQuantity/{id}?quantity={quantity}", id, quantity)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error decreasing product quantity")))
                .bodyToMono(Product.class);
    }
    


}