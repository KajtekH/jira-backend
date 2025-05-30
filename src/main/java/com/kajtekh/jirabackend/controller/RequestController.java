package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.facade.RequestFacade;
import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.request.dto.RequestRequest;
import com.kajtekh.jirabackend.model.request.dto.RequestResponse;
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

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestFacade requestFacade;

    public RequestController(final RequestFacade requestFacade) {
        this.requestFacade = requestFacade;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<RequestResponse>> getAllRequests(@PathVariable final Long productId) {
        return ResponseEntity.ok(requestFacade.getAllRequests(productId));
    }

    @GetMapping("/request/{id}")
    public ResponseEntity<RequestResponse> getRequestById(@PathVariable final Long id) {
        return ResponseEntity.ok(requestFacade.getRequestById(id));
    }

    @PostMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ACCOUNT_MANAGER')")
    public ResponseEntity<RequestResponse> addRequest(@RequestBody final RequestRequest requestRequest, @PathVariable final Long productId) {
        final var requestResponse = requestFacade.addRequest(requestRequest, productId);
        return ResponseEntity.status(CREATED).body(requestResponse);
    }

    @PatchMapping("/{id}/{status}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_ACCOUNT_MANAGER') and @requestFacade.isAssignedToRequest(#id, authentication))")
    public ResponseEntity<RequestResponse> updateStatus(@PathVariable final Long id, @PathVariable final Status status, @RequestBody(required = false) final String result) {
        final var requestResponse = requestFacade.updateStatus(id, status, result);
        return ResponseEntity.ok(requestResponse);
    }

}
