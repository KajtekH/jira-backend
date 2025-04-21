package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.facade.IssueFacade;
import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.issue.dto.IssueRequest;
import com.kajtekh.jirabackend.model.issue.dto.IssueResponse;
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
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueFacade issueFacade;

    public IssueController(final IssueFacade issueFacade) {
        this.issueFacade = issueFacade;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<IssueResponse>> getAllIssues(@PathVariable final Long id) {
        final var issues = issueFacade.getAllIssues(id);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("issue/{id}")
    public ResponseEntity<IssueResponse> getIssueById(@PathVariable final Long id) {
        final var issueResponse = issueFacade.getIssueById(id);
        return ResponseEntity.ok(issueResponse);
    }

    @PatchMapping("/{id}/{status}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PRODUCT_MANAGER') and @issueFacade.isAssignedToIssue(#id, authentication))")
    public ResponseEntity<IssueResponse> updateStatus(@PathVariable final Long id, @PathVariable final Status status) {
        final var issueResponse = issueFacade.updateStatus(id, status);
        return ResponseEntity.ok(issueResponse);
    }

    @PostMapping("/{requestId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_ACCOUNT_MANAGER')  and @issueFacade.isAssignedToRequest(#requestId, authentication))")
    public ResponseEntity<IssueResponse> addIssue(@RequestBody final IssueRequest issueRequest, @PathVariable final Long requestId) {
        final var issueResponse = issueFacade.addIssue(issueRequest, requestId);
        return ResponseEntity.status(CREATED).body(issueResponse);
    }


}
