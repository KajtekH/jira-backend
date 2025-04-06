package com.kajtekh.jirabackend.model.task.dto;

import com.kajtekh.jirabackend.model.task.Task;
import com.kajtekh.jirabackend.model.user.User;

import java.util.Optional;

public record TaskResponse(Long id, String name, String description, String assignee, String type, String status, String createdAt, String updatedAt, int priority) {
    public static TaskResponse fromTask(final Task task) {
        return new TaskResponse(
                task.getId(),
                task.getName(),
                task.getDescription(),
                Optional.ofNullable(task.getAssignee()).map(User::getUsername).orElse(""),
                task.getTaskType().getName(),
                task.getStatus().name(),
                task.getCreatedAt().toString(),
                task.getUpdatedAt().toString(),
                task.getPriority()
        );
    }
}
