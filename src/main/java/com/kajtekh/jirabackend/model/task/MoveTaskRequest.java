package com.kajtekh.jirabackend.model.task;

public record MoveTaskRequest(Long taskId, Status status) {
}
