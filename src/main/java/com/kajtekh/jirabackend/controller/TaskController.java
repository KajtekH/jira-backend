package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.model.task.MoveTaskRequest;
import com.kajtekh.jirabackend.model.task.Status;
import com.kajtekh.jirabackend.model.task.Task;
import com.kajtekh.jirabackend.model.task.TaskRequest;
import com.kajtekh.jirabackend.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping()
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping()
    public ResponseEntity<Task> addTask(@RequestBody TaskRequest taskRequest) {
        final var task = taskService.addTask(taskRequest);
        return ResponseEntity.ok(task);
    }

    @PatchMapping()
    public ResponseEntity<Task> moveTask(@RequestBody MoveTaskRequest moveTaskRequest) {
        final var task = taskService.moveTask(moveTaskRequest.taskId(), moveTaskRequest.status());
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
