package com.kajtekh.jirabackend.model.task.dto;

public record TaskRequest(String name, String description, String assignee, String taskType) {
}
