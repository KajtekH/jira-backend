package com.kajtekh.jirabackend.model.task.dto;

import com.kajtekh.jirabackend.model.task.TaskType;

public record TaskRequest(String name, String description, String assignee, TaskType taskType) {
}
