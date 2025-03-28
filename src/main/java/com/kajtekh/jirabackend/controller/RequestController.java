package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.request.dto.RequestRequest;
import com.kajtekh.jirabackend.model.request.dto.RequestResponse;
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

import static com.kajtekh.jirabackend.model.request.dto.RequestResponse.fromRequest;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;
    private final UserService userService;

    public RequestController(RequestService requestService, UserService userService) {
        this.requestService = requestService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<RequestResponse>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllRequests().stream().map(RequestResponse::fromRequest).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestResponse> getRequestById(@PathVariable Long id) {
        final var requestResponse = fromRequest(requestService.getRequestById(id));
        return ResponseEntity.ok(requestResponse);
    }

    @PostMapping
    public ResponseEntity<RequestResponse> addRequest(@RequestBody RequestRequest requestRequest) {
        final var accountManager = userService.getUserByUsername(requestRequest.accountManager());
        final var requestResponse = fromRequest(requestService.addRequest(requestRequest, accountManager));
        return ResponseEntity.ok(requestResponse);
    }

    @PatchMapping("/{id}/{status}")
    public ResponseEntity<RequestResponse> updateStatus(@PathVariable Long id, @PathVariable Status status) {
        final var requestResponse = fromRequest(requestService.updateStatus(id, status));
        return ResponseEntity.ok(requestResponse);
    }

}
