package com.kajtekh.jirabackend.model.task;

public record TaskRequest(String name, String description, String assignee, Type type) {
}
