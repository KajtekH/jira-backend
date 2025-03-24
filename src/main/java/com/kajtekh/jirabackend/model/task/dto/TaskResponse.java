package com.kajtekh.jirabackend.model.task.dto;

import com.kajtekh.jirabackend.model.task.Task;

public record TaskResponse(Long id, String name, String description, String assignee, String type, String status) {
    public static TaskResponse fromTask(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getAssignee().getUsername(),
                task.getType().name(),
                task.getTaskStatus().name()
        );
    }
}
