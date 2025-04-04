package com.kajtekh.jirabackend.model.task.dto;

import com.kajtekh.jirabackend.model.task.Task;

public record TaskResponse(Long id, String name, String description, String assignee, String type, String status, String createdAt, String updatedAt) {
    public static TaskResponse fromTask(final Task task) {
        return new TaskResponse(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getAssignee().getUsername(),
                task.getTaskType().getName(),
                task.getStatus().name(),
                task.getCreatedAt().toString(),
                task.getUpdatedAt().toString()
        );
    }
}
