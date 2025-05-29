package com.kajtekh.jirabackend.facade;

import com.kajtekh.jirabackend.model.task.dto.MoveTaskRequest;
import com.kajtekh.jirabackend.model.task.dto.TaskListResponse;
import com.kajtekh.jirabackend.model.task.dto.TaskRequest;
import com.kajtekh.jirabackend.model.task.dto.TaskResponse;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.service.IssueService;
import com.kajtekh.jirabackend.service.TaskService;
import com.kajtekh.jirabackend.service.UpdateNotificationService;
import com.kajtekh.jirabackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class TaskFacade {
    private static final Logger LOG = LoggerFactory.getLogger(TaskFacade.class);
    private static final String CACHE_EVICTED_MSG = "Cache evicted for key: tasks{}";
    private static final String TASKS_CACHE_KEY = "tasks";

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
        final var taskListResponse = taskService.getAllTasksByIssue(issueId);
        LOG.debug("Fetching all tasks for issue with ID: '{}' {}", issueId, taskListResponse);
        return taskListResponse;
    }

    public TaskResponse addTask(final TaskRequest taskRequest, final Long issueId) {
        LOG.debug("Adding task for issue with ID: '{}' with request: {}", issueId, taskRequest);
        final var issue = issueService.getIssueById(issueId);
        final var assignee = userService.getWorker(taskRequest.assignee());
        final var task = taskService.addTask(taskRequest, issue, assignee);
        cache.evictIfPresent(TASKS_CACHE_KEY+ issueId);
        LOG.trace(CACHE_EVICTED_MSG, issueId);
        updateNotificationService.notifyTaskListUpdate(issueId);
        return TaskResponse.fromTask(task);
    }

    public TaskResponse updateTask(final Long id, final TaskRequest taskRequest) {
        LOG.debug("Updating task with ID: '{}' and request: {}", id, taskRequest);
        final var assignee = userService.getWorker(taskRequest.assignee());
        final var task = taskService.updateTask(id, taskRequest, assignee);
        cache.evictIfPresent(TASKS_CACHE_KEY + task.getIssue().getId());
        LOG.trace(CACHE_EVICTED_MSG, task.getIssue().getId());
        updateNotificationService.notifyTaskListUpdate(task.getIssue().getId());
        return TaskResponse.fromTask(task);
    }

    public TaskResponse moveTask(final MoveTaskRequest moveTaskRequest) {
        LOG.debug("Moving task with ID: '{}' to status: {}", moveTaskRequest.taskId(), moveTaskRequest.status());
        final var task = taskService.moveTask(moveTaskRequest);
        cache.evictIfPresent(TASKS_CACHE_KEY + task.getIssue().getId());
        LOG.trace(CACHE_EVICTED_MSG, task.getIssue().getId());
        updateNotificationService.notifyTaskListUpdate(task.getIssue().getId());
        return TaskResponse.fromTask(task);
    }

    public boolean  isAssignedToTask(final MoveTaskRequest moveTaskRequest, final Authentication authentication) {
        final var task = taskService.getTaskById(moveTaskRequest.taskId());
        final var user = (User) authentication.getPrincipal();
        return task.getAssignee().getId().equals(user.getId());
    }

    public boolean isAssignedToIssue(final Long issueId, final Authentication authentication) {
        final var issue = issueService.getIssueById(issueId);
        final var user = (User) authentication.getPrincipal();
        return issue.getProductManager().getId().equals(user.getId());
    }

    public boolean isIssueManager(final Long taskId, final Authentication authentication) {
        final var task = taskService.getTaskById(taskId);
        final var issue = task.getIssue();
        final var user = (User) authentication.getPrincipal();
        return issue.getProductManager().getId().equals(user.getId());
    }

}
