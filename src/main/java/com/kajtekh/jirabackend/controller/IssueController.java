package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.issue.dto.IssueRequest;
import com.kajtekh.jirabackend.model.issue.dto.IssueResponse;
import com.kajtekh.jirabackend.service.IssueService;
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

import static com.kajtekh.jirabackend.model.issue.dto.IssueResponse.fromIssue;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;
    private final UserService userService;

    public IssueController(IssueService issueService, UserService userService) {
        this.issueService = issueService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<IssueResponse>> getAllIssues() {
        return ResponseEntity.ok(issueService.getAllIssues());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getIssueById(@PathVariable Long id) {
        final var issueResponse = fromIssue(issueService.getIssueById(id));
        return ResponseEntity.ok(issueResponse);
    }


    @PatchMapping("/{id}/{status}")
    public ResponseEntity<IssueResponse> updateStatus(@PathVariable Long id, @PathVariable Status status) {
        final var issueResponse = fromIssue(issueService.updateStatus(id, status));
        return ResponseEntity.ok(issueResponse);
    }

    @PostMapping
    public ResponseEntity<IssueResponse> addIssue(@RequestBody IssueRequest issueRequest) {
        final var productManager = userService.getUserByUsername(issueRequest.productManager());
        final var issueResponse = fromIssue(issueService.addIssue(issueRequest, productManager));
        return ResponseEntity.ok(issueResponse);
    }


}
