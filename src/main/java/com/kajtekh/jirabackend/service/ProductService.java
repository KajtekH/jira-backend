package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.product.Product;
import com.kajtekh.jirabackend.model.product.dto.ProductRequest;
import com.kajtekh.jirabackend.model.request.RequestType;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product addProduct(final ProductRequest productRequest, final User owner) {
        final var product = new Product();
        product.setName(productRequest.name());
        product.setDescription(productRequest.description());
        product.setOwner(owner);
        product.setVersion("0.0.0");
        return productRepository.save(product);
    }

    public Product getProductById(final Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void bumpVersion(final Product product, final RequestType requestType) {
        final var version = product.getVersion().split("\\.");
        final var major = Integer.parseInt(version[0]);
        final var minor = Integer.parseInt(version[1]);
        final var patch = Integer.parseInt(version[2]);
        switch (requestType) {
            case MAJOR -> product.setVersion(String.format("%d.%d.%d", major + 1, 0, 0));
            case MINOR -> product.setVersion(String.format("%d.%d.%d", major, minor + 1, 0));
            case PATCH -> product.setVersion(String.format("%d.%d.%d", major, minor, patch + 1));
        }
        product.setReleaseDate(LocalDateTime.now().truncatedTo(MINUTES));
        productRepository.save(product);
    }
}
