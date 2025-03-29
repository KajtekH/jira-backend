package com.kajtekh.jirabackend.model.product.dto;

import com.kajtekh.jirabackend.model.product.Product;

import java.util.Optional;

public record ProductResponse(Long id, String name, String description, String version, String releaseDate, String owner) {
    public static ProductResponse fromProduct(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getVersion(),
                Optional.ofNullable(product.getReleaseDate()).map(Object::toString).orElse(null),
                product.getOwner().getUsername()
        );
    }
}
