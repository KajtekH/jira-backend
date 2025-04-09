package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.issue.dto.IssueRequest;
import com.kajtekh.jirabackend.model.issue.dto.IssueResponse;
import com.kajtekh.jirabackend.service.IssueService;
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

import static com.kajtekh.jirabackend.model.issue.dto.IssueResponse.fromIssue;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;
    private final UserService userService;
    private final RequestService requestService;
    private final UpdateNotificationService updateNotificationService;

    public IssueController(final IssueService issueService, final UserService userService, final RequestService requestService,
                           final UpdateNotificationService updateNotificationService) {
        this.issueService = issueService;
        this.userService = userService;
        this.requestService = requestService;
        this.updateNotificationService = updateNotificationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<IssueResponse>> getAllIssues(@PathVariable final Long id) {
        return ResponseEntity.ok(issueService.getAllIssues(id));
    }

    @GetMapping("issue/{id}")
    public ResponseEntity<IssueResponse> getIssueById(@PathVariable final Long id) {
        final var issueResponse = fromIssue(issueService.getIssueById(id));
        return ResponseEntity.ok(issueResponse);
    }

    @PatchMapping("/{id}/{status}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRODUCT_MANAGER')")
    public ResponseEntity<IssueResponse> updateStatus(@PathVariable final Long id, @PathVariable final Status status) {
        final var issue = issueService.updateStatus(id, status);
        updateNotificationService.notifyIssueListUpdate(issue.getRequest().getId());
        return ResponseEntity.ok(fromIssue(issue));
    }

    @PostMapping("/{requestId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ACCOUNT_MANAGER')")
    public ResponseEntity<IssueResponse> addIssue(@RequestBody final IssueRequest issueRequest, @PathVariable final Long requestId) {
        final var productManager = userService.getUserByUsername(issueRequest.productManager());
        final var request = requestService.getRequestById(requestId);
        final var issueResponse = fromIssue(issueService.addIssue(issueRequest, productManager, request));
        return ResponseEntity.status(CREATED).body(issueResponse);
    }


}
