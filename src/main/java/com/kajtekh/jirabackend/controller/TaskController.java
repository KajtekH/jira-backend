package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.model.Status;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.kajtekh.jirabackend.model.task.dto.TaskResponse.fromTask;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private static final Logger LOG = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;
    private final UserService userService;
    private final IssueService issueService;
    private final UpdateNotificationService updateNotificationService;

    public TaskController(final TaskService taskService, final UserService userService, final IssueService issueService, final UpdateNotificationService updateNotificationService) {
        this.taskService = taskService;
        this.userService = userService;
        this.issueService = issueService;
        this.updateNotificationService = updateNotificationService;
    }

    @GetMapping()
    @Cacheable(value = "allTasks")
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{issueId}")
    public ResponseEntity<TaskListResponse> getAllTasksByIssue(@PathVariable final Long issueId) {
        LOG.info("Fetching all tasks for issue with ID: {}", issueId);
        final var tasks = taskService.getAllTasksByIssue(issueId);
        LOG.info("Tasks for return: {}", tasks);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{issueId}/{status}")
    //@Cacheable(value = "tasksByStatus", key = "T(String).valueOf(#issueId) + T(String).valueOf(#status)")
    public List<TaskResponse> getTasksByStatus(@PathVariable final Status status, @PathVariable final Long issueId) {
        return taskService.getTasksByStatus(status, issueId);
    }

    @PostMapping("/{issueId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRODUCT_MANAGER')")
    public ResponseEntity<TaskResponse> addTask(@PathVariable final Long issueId, @RequestBody final TaskRequest taskRequest) {
        final var issue = issueService.getIssueById(issueId);
        final var assignee = userService.getUserByUsername(taskRequest.assignee());
        final var taskResponse = fromTask(taskService.addTask(taskRequest, issue, assignee));
        updateNotificationService.notifyTaskListUpdate(issueId);
        return ResponseEntity.status(CREATED).body(taskResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRODUCT_MANAGER')")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable final Long id, @RequestBody final TaskRequest taskRequest) {
        final var assignee = userService.getUserByUsername(taskRequest.assignee());
        final var task = taskService.updateTask(id, taskRequest, assignee);
        updateNotificationService.notifyTaskListUpdate(task.getIssue().getId());
        return ResponseEntity.ok(fromTask(task));
    }

    @PatchMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_WORKER')")
    public ResponseEntity<TaskResponse> moveTask(@RequestBody final MoveTaskRequest moveTaskRequest) {
        final var task = taskService.moveTask(moveTaskRequest.taskId(), moveTaskRequest.status());
        updateNotificationService.notifyTaskListUpdate(task.getIssue().getId());
        return ResponseEntity.ok(fromTask(task));
    }

    @Profile("test")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable final Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Profile("test")
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRODUCT_MANAGER')")
    public ResponseEntity<TaskResponse> addTask(@RequestBody final TaskRequest taskRequest) {
        final var assignee = userService.getUserByUsername(taskRequest.assignee());
        final var taskResponse = fromTask(taskService.addTask(taskRequest, null, assignee));
        return ResponseEntity.status(CREATED).body(taskResponse);
    }
}
