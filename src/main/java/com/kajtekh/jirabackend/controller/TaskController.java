package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.model.task.dto.MoveTaskRequest;
import com.kajtekh.jirabackend.model.task.dto.TaskResponse;
import com.kajtekh.jirabackend.model.task.TaskStatus;
import com.kajtekh.jirabackend.model.task.Task;
import com.kajtekh.jirabackend.model.task.dto.TaskRequest;
import com.kajtekh.jirabackend.service.IssueService;
import com.kajtekh.jirabackend.service.TaskService;
import com.kajtekh.jirabackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{taskStatus}")
    public List<TaskResponse> getTasksByStatus(@PathVariable TaskStatus taskStatus) {
        return taskService.getTasksByStatus(taskStatus);
    }

    @PostMapping("/{issueId}")
    public ResponseEntity<TaskResponse> addTask(@PathVariable Long issueId, @RequestBody TaskRequest taskRequest) {
        final var issue = issueService.getIssueById(issueId);
        final var assignee = userService.getUserByUsername(taskRequest.assignee());
        final var taskResponse = TaskResponse.fromTask(taskService.addTask(taskRequest, issue, assignee));
        return ResponseEntity.ok(taskResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskRequest taskRequest) {
        final var assignee = userService.getUserByUsername(taskRequest.assignee());
        final var taskResponse = TaskResponse.fromTask(taskService.updateTask(id, taskRequest, assignee));
        return ResponseEntity.ok(taskResponse);
    }

    @PatchMapping()
    public ResponseEntity<TaskResponse> moveTask(@RequestBody MoveTaskRequest moveTaskRequest) {
        final var taskResponse = TaskResponse.fromTask(taskService.moveTask(moveTaskRequest.taskId(), moveTaskRequest.taskStatus()));
        return ResponseEntity.ok(taskResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
