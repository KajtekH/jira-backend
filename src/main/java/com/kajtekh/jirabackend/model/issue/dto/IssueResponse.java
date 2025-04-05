package com.kajtekh.jirabackend.model.issue.dto;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.issue.Issue;

import java.util.Optional;

import static com.kajtekh.jirabackend.model.Status.CLOSED;

public record IssueResponse(Long id, String name, String description, String openDate, String closeDate, Status status, String productManager, int tasksCount, int doneTasksCount) {
    public static IssueResponse fromIssue(final Issue issue) {
        return new IssueResponse(
                issue.getId(),
                issue.getName(),
                issue.getDescription(),
                issue.getOpenDate().toString(),
                Optional.ofNullable(issue.getCloseDate()).map(Object::toString).orElse(null),
                issue.getStatus(),
                issue.getProductManager().getUsername(),
                issue.getTasks().size(),
                (int) issue.getTasks().stream().filter(task -> task.getStatus().equals(CLOSED)).count()
        );
    }
}
