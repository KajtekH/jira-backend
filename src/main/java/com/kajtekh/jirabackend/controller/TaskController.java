package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.model.task.dto.MoveTaskRequest;
import com.kajtekh.jirabackend.model.task.dto.TaskResponse;
import com.kajtekh.jirabackend.model.task.TaskStatus;
import com.kajtekh.jirabackend.model.task.Task;
import com.kajtekh.jirabackend.model.task.dto.TaskRequest;
import com.kajtekh.jirabackend.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping()
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{taskStatus}")
    public List<TaskResponse> getTasksByStatus(@PathVariable TaskStatus taskStatus) {
        return taskService.getTasksByStatus(taskStatus);
    }

    @PostMapping()
    public ResponseEntity<TaskResponse> addTask(@RequestBody TaskRequest taskRequest) {
        final var taskResponse = TaskResponse.fromTask(taskService.addTask(taskRequest));
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
