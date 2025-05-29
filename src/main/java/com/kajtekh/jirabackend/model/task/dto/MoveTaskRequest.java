package com.kajtekh.jirabackend.model.task.dto;

import com.kajtekh.jirabackend.model.Status;

public record MoveTaskRequest(Long taskId, Status status, String result) {
}
