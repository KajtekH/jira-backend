package com.kajtekh.jirabackend.facade;

import com.kajtekh.jirabackend.model.task.dto.MoveTaskRequest;
import com.kajtekh.jirabackend.model.task.dto.TaskListResponse;
import com.kajtekh.jirabackend.model.task.dto.TaskRequest;
import com.kajtekh.jirabackend.model.task.dto.TaskResponse;
import com.kajtekh.jirabackend.service.IssueService;
import com.kajtekh.jirabackend.service.TaskService;
import com.kajtekh.jirabackend.service.UpdateNotificationService;
import com.kajtekh.jirabackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class TaskFacade {
    private static final Logger LOG = LoggerFactory.getLogger(TaskFacade.class);

    private final TaskService taskService;
    private final UserService userService;
    private final IssueService issueService;
    private final UpdateNotificationService updateNotificationService;
    private final Cache cache;

    public TaskFacade(final TaskService taskService, final UserService userService, final IssueService issueService, final UpdateNotificationService updateNotificationService, final Cache cache) {
        this.taskService = taskService;
        this.userService = userService;
        this.issueService = issueService;
        this.updateNotificationService = updateNotificationService;
        this.cache = cache;
    }

    @Cacheable(value = "data", key = "'tasks' + #issueId")
    @Caching()
    public TaskListResponse getAllTasksByIssue(final Long issueId) {
        return taskService.getAllTasksByIssue(issueId);
    }

    public TaskResponse addTask(final TaskRequest taskRequest, final Long issueId) {
        final var issue = issueService.getIssueById(issueId);
        final var assignee = userService.getUserByUsername(taskRequest.assignee());
        final var task = taskService.addTask(taskRequest, issue, assignee);
        cache.evictIfPresent("tasks" + issueId);
        updateNotificationService.notifyTaskListUpdate(issueId);
        return TaskResponse.fromTask(task);
    }

    public TaskResponse updateTask(final Long id, final TaskRequest taskRequest) {
        final var assignee = userService.getUserByUsername(taskRequest.assignee());
        final var task = taskService.updateTask(id, taskRequest, assignee);
        cache.evictIfPresent("tasks" + task.getIssue().getId());
        updateNotificationService.notifyTaskListUpdate(task.getIssue().getId());
        return TaskResponse.fromTask(task);
    }

    public TaskResponse moveTask(final MoveTaskRequest moveTaskRequest) {
        final var task = taskService.moveTask(moveTaskRequest.taskId(), moveTaskRequest.status());
        cache.evictIfPresent("tasks" + task.getIssue().getId());
        updateNotificationService.notifyTaskListUpdate(task.getIssue().getId());
        return TaskResponse.fromTask(task);
    }

}
