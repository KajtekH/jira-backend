package com.kajtekh.jirabackend.model.task.dto;

import com.kajtekh.jirabackend.model.task.Type;

public record TaskRequest(String name, String description, String assignee, Type type) {
}
