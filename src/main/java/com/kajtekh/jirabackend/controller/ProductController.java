package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.model.product.dto.ProductRequest;
import com.kajtekh.jirabackend.model.product.dto.ProductResponse;
import com.kajtekh.jirabackend.service.ProductService;
import com.kajtekh.jirabackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.kajtekh.jirabackend.model.product.dto.ProductResponse.fromProduct;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts().stream().map(ProductResponse::fromProduct).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(fromProduct(productService.getProductById(id)));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest productRequest) {
        final var owner = userService.getUserByUsername(productRequest.owner());
        return ResponseEntity.status(CREATED).body(fromProduct(productService.addProduct(productRequest, owner)));
    }
}
