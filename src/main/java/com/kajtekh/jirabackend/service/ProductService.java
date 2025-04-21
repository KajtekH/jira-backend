package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.exception.ProductNotFoundException;
import com.kajtekh.jirabackend.model.product.Product;
import com.kajtekh.jirabackend.model.product.dto.ProductRequest;
import com.kajtekh.jirabackend.model.request.RequestType;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);
    private static final String VERSION_PATTERN = "%d.%d.%d";
    private final ProductRepository productRepository;

    public ProductService(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public Product addProduct(final ProductRequest productRequest, final User owner) {
        final var product = new Product();
        product.setName(productRequest.name());
        product.setDescription(productRequest.description());
        product.setOwner(owner);
        product.setVersion("0.0.0");
        productRepository.save(product);
        LOG.info("Product added successfully: {}", product);
        return product;
    }

    @Transactional(readOnly = true)
    public Product getProductById(final Long id) {
        return productRepository.findById(id).orElseThrow(() -> {
            LOG.warn("Product with ID '{}' not found", id);
            return new ProductNotFoundException("Product not found");
        });
    }

    @Transactional
    public void bumpVersion(final Product product, final RequestType requestType) {
        final var previousVersion = product.getVersion();
        final var versionArr = previousVersion.split("\\.");
        final var major = Integer.parseInt(versionArr[0]);
        final var minor = Integer.parseInt(versionArr[1]);
        final var patch = Integer.parseInt(versionArr[2]);
        switch (requestType) {
            case MAJOR -> product.setVersion(String.format(VERSION_PATTERN, major + 1, 0, 0));
            case MINOR -> product.setVersion(String.format(VERSION_PATTERN, major, minor + 1, 0));
            case PATCH -> product.setVersion(String.format(VERSION_PATTERN, major, minor, patch + 1));
        }
        product.setReleaseDate(LocalDateTime.now().truncatedTo(MINUTES));
        productRepository.save(product);
        LOG.info("Product version for product with ID '{}' bumped from '{}' to '{}'",
                product.getId(), previousVersion, product.getVersion());
    }
}
