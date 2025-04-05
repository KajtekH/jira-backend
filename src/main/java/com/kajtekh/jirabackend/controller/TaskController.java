package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.task.dto.MoveTaskRequest;
import com.kajtekh.jirabackend.model.task.dto.TaskRequest;
import com.kajtekh.jirabackend.model.task.dto.TaskResponse;
import com.kajtekh.jirabackend.service.IssueService;
import com.kajtekh.jirabackend.service.TaskService;
import com.kajtekh.jirabackend.service.UserService;
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

    private final TaskService taskService;
    private final UserService userService;
    private final IssueService issueService;

    public TaskController(TaskService taskService, UserService userService, IssueService issueService) {
        this.taskService = taskService;
        this.userService = userService;
        this.issueService = issueService;
    }

    @GetMapping()
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{issueId}/{status}")
    public List<TaskResponse> getTasksByStatus(@PathVariable Status status, @PathVariable Long issueId) {
        return taskService.getTasksByStatus(status, issueId);
    }

    @PostMapping("/{issueId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRODUCT_MANAGER')")
    public ResponseEntity<TaskResponse> addTask(@PathVariable Long issueId, @RequestBody TaskRequest taskRequest) {
        final var issue = issueService.getIssueById(issueId);
        final var assignee = userService.getUserByUsername(taskRequest.assignee());
        final var taskResponse = fromTask(taskService.addTask(taskRequest, issue, assignee));
        return ResponseEntity.status(CREATED).body(taskResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRODUCT_MANAGER')")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest) {
        final var assignee = userService.getUserByUsername(taskRequest.assignee());
        final var taskResponse = fromTask(taskService.updateTask(id, taskRequest, assignee));
        return ResponseEntity.ok(taskResponse);
    }

    @PatchMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_WORKER')")
    public ResponseEntity<TaskResponse> moveTask(@RequestBody MoveTaskRequest moveTaskRequest) {
        final var taskResponse = fromTask(taskService.moveTask(moveTaskRequest.taskId(), moveTaskRequest.status()));
        return ResponseEntity.ok(taskResponse);
    }

    @Profile("test")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Profile("test")
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRODUCT_MANAGER')")
    public ResponseEntity<TaskResponse> addTask(@RequestBody TaskRequest taskRequest) {
        final var assignee = userService.getUserByUsername(taskRequest.assignee());
        final var taskResponse = fromTask(taskService.addTask(taskRequest, null, assignee));
        return ResponseEntity.status(CREATED).body(taskResponse);
    }
}
