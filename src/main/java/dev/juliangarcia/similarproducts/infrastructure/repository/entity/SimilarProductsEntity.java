package dev.juliangarcia.similarproducts.infrastructure.repository.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;

public record SimilarProductsEntity(
    @JsonValue List<String> productIds) {
}
