package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.request.dto.RequestRequest;
import com.kajtekh.jirabackend.model.request.dto.RequestResponse;
import com.kajtekh.jirabackend.service.ProductService;
import com.kajtekh.jirabackend.service.RequestService;
import com.kajtekh.jirabackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.kajtekh.jirabackend.model.Status.CLOSED;
import static com.kajtekh.jirabackend.model.request.dto.RequestResponse.fromRequest;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;
    private final UserService userService;
    private final ProductService productService;

    public RequestController(RequestService requestService, UserService userService, ProductService ProductService) {
        this.requestService = requestService;
        this.userService = userService;
        this.productService = ProductService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<RequestResponse>> getAllRequests(@PathVariable Long productId) {
        return ResponseEntity.ok(requestService.getAllRequests(productId).stream().map(RequestResponse::fromRequest).toList());
    }

    @GetMapping("/request/{id}")
    public ResponseEntity<RequestResponse> getRequestById(@PathVariable Long id) {
        final var requestResponse = fromRequest(requestService.getRequestById(id));
        return ResponseEntity.ok(requestResponse);
    }

    @PostMapping("/{productId}")
    public ResponseEntity<RequestResponse> addRequest(@RequestBody RequestRequest requestRequest, @PathVariable Long productId) {
        final var accountManager = userService.getUserByUsername(requestRequest.accountManager());
        final var product = productService.getProductById(productId);
        final var requestResponse = fromRequest(requestService.addRequest(requestRequest, accountManager, product));
        return ResponseEntity.status(CREATED).body(requestResponse);
    }

    @PatchMapping("/{id}/{status}")
    public ResponseEntity<RequestResponse> updateStatus(@PathVariable Long id, @PathVariable Status status) {
        final var request = requestService.updateStatus(id, status);
        final var requestResponse = fromRequest(request);
        if (status == CLOSED) {
            productService.bumpVersion(request.getProduct(), request.getRequestType());
        }
        return ResponseEntity.ok(requestResponse);
    }

}
