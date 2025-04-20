package com.kajtekh.jirabackend.facade;

import com.kajtekh.jirabackend.model.product.dto.ProductRequest;
import com.kajtekh.jirabackend.model.product.dto.ProductResponse;
import com.kajtekh.jirabackend.service.ProductService;
import com.kajtekh.jirabackend.service.UpdateNotificationService;
import com.kajtekh.jirabackend.service.UserService;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductFacade {

    private final ProductService productService;
    private final UserService userService;
    private final UpdateNotificationService updateNotificationService;
    private final Cache cache;

    public ProductFacade(final ProductService productService, final UserService userService,
                         final UpdateNotificationService updateNotificationService, final Cache cache) {
        this.productService = productService;
        this.userService = userService;
        this.updateNotificationService = updateNotificationService;
        this.cache = cache;
    }

    @Cacheable(value = "data", key = "'products'")
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts().stream()
                .map(ProductResponse::fromProduct)
                .toList();
    }

    @Cacheable(value = "data", key = "'product' + #id")
    public ProductResponse getProductById(final Long id) {
        return ProductResponse.fromProduct(productService.getProductById(id));
    }

    public ProductResponse addProduct(final ProductRequest productRequest) {
        final var owner = userService.getUserByUsername(productRequest.owner());
        final var product = productService.addProduct(productRequest, owner);
        cache.evictIfPresent("products");
        cache.put("product" + product.getId(), ProductResponse.fromProduct(product));
        updateNotificationService.notifyProductListUpdate();
        return ProductResponse.fromProduct(product);
    }
}
