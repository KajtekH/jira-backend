package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.model.issue.dto.IssueRequest;
import com.kajtekh.jirabackend.model.issue.dto.IssueResponse;
import com.kajtekh.jirabackend.service.IssueService;
import com.kajtekh.jirabackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;
    private final UserService userService;

    public IssueController(IssueService issueService, UserService userService) {
        this.issueService = issueService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<IssueResponse> addIssue(@RequestBody IssueRequest issueRequest) {
        final var productManager = userService.getUserByUsername(issueRequest.productManager());
        final var issueResponse = IssueResponse.fromIssue(issueService.addIssue(issueRequest, productManager));
        return ResponseEntity.ok(issueResponse);
    }
}
