package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.request.dto.RequestRequest;
import com.kajtekh.jirabackend.model.request.dto.RequestResponse;
import com.kajtekh.jirabackend.service.ProductService;
import com.kajtekh.jirabackend.service.RequestService;
import com.kajtekh.jirabackend.service.UpdateNotificationService;
import com.kajtekh.jirabackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final UpdateNotificationService updateNotificationService;

    public RequestController(final RequestService requestService, final UserService userService, final ProductService ProductService,
                             final UpdateNotificationService updateNotificationService) {
        this.requestService = requestService;
        this.userService = userService;
        this.productService = ProductService;
        this.updateNotificationService = updateNotificationService;
    }


    @GetMapping("/{productId}")
    public ResponseEntity<List<RequestResponse>> getAllRequests(@PathVariable final Long productId) {
        return ResponseEntity.ok(requestService.getAllRequests(productId).stream().map(RequestResponse::fromRequest).toList());
    }

    @GetMapping("/request/{id}")
    public ResponseEntity<RequestResponse> getRequestById(@PathVariable final Long id) {
        final var requestResponse = fromRequest(requestService.getRequestById(id));
        return ResponseEntity.ok(requestResponse);
    }

    @PostMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ACCOUNT_MANAGER')")
    public ResponseEntity<RequestResponse> addRequest(@RequestBody final RequestRequest requestRequest, @PathVariable final Long productId) {
        final var accountManager = userService.getUserByUsername(requestRequest.accountManager());
        final var product = productService.getProductById(productId);
        final var requestResponse = fromRequest(requestService.addRequest(requestRequest, accountManager, product));
        updateNotificationService.notifyRequestListUpdate(productId);
        return ResponseEntity.status(CREATED).body(requestResponse);
    }

    @PatchMapping("/{id}/{status}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ACCOUNT_MANAGER')")
    public ResponseEntity<RequestResponse> updateStatus(@PathVariable final Long id, @PathVariable final Status status) {
        final var request = requestService.updateStatus(id, status);
        final var requestResponse = fromRequest(request);
        if (status == CLOSED) {
            productService.bumpVersion(request.getProduct(), request.getRequestType());
            updateNotificationService.notifyProductListUpdate();
        }
        updateNotificationService.notifyRequestListUpdate(request.getProduct().getId());
        return ResponseEntity.ok(requestResponse);
    }

}
