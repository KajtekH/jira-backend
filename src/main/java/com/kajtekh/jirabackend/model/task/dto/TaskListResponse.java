package com.kajtekh.jirabackend.model.task.dto;

import java.util.List;

public record TaskListResponse(List<TaskResponse> openTasks,
                               List<TaskResponse> inProgressTasks,
                               List<TaskResponse> closedTasks,
                               List<TaskResponse> abandonedTasks) {

}
