package com.kajtekh.jirabackend.controller;

import com.kajtekh.jirabackend.facade.TaskFacade;
import com.kajtekh.jirabackend.model.task.dto.MoveTaskRequest;
import com.kajtekh.jirabackend.model.task.dto.TaskListResponse;
import com.kajtekh.jirabackend.model.task.dto.TaskRequest;
import com.kajtekh.jirabackend.model.task.dto.TaskResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskFacade taskFacade;

    public TaskController(final TaskFacade taskFacade) {
        this.taskFacade = taskFacade;
    }


    @GetMapping("/{issueId}")
    public ResponseEntity<TaskListResponse> getAllTasksByIssue(@PathVariable final Long issueId) {
        final var tasks = taskFacade.getAllTasksByIssue(issueId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/{issueId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PRODUCT_MANAGER') and @taskFacade.isAssignedToIssue(#issueId, authentication))")
    public ResponseEntity<TaskResponse> addTask(@PathVariable final Long issueId, @RequestBody final TaskRequest taskRequest) {
        final var taskResponse = taskFacade.addTask(taskRequest, issueId);
        return ResponseEntity.status(CREATED).body(taskResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_PRODUCT_MANAGER') and @taskFacade.isIssueManager(#id, authentication))")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable final Long id, @RequestBody final TaskRequest taskRequest) {
        final var taskResponse = taskFacade.updateTask(id, taskRequest);
        return ResponseEntity.ok(taskResponse);
    }

    @PatchMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN' ) or (hasRole('ROLE_WORKER') and @taskFacade.isAssignedToTask(#moveTaskRequest, authentication))")
    public ResponseEntity<TaskResponse> moveTask(@RequestBody final MoveTaskRequest moveTaskRequest) {
        final var taskResponse = taskFacade.moveTask(moveTaskRequest);
        return ResponseEntity.ok(taskResponse);
    }

}
