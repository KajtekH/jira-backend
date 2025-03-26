package com.kajtekh.jirabackend.model.issue.dto;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.issue.Issue;

public record IssueResponse(Long id, String name, String description, String openDate, String closeDate, Status status, String productManager) {
    public static IssueResponse fromIssue(Issue issue) {
        return new IssueResponse(
                issue.getId(),
                issue.getName(),
                issue.getDescription(),
                issue.getOpenDate().toString(),
                issue.getCloseDate().toString(),
                issue.getStatus(),
                issue.getProductManager().getUsername()
        );
    }
}
