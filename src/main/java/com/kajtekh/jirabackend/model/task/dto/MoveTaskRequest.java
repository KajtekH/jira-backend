package com.kajtekh.jirabackend.model.task.dto;

import com.kajtekh.jirabackend.model.task.TaskStatus;

public record MoveTaskRequest(Long taskId, TaskStatus taskStatus) {
}
