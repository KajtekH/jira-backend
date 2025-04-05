package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.issue.Issue;
import com.kajtekh.jirabackend.model.task.Task;
import com.kajtekh.jirabackend.model.task.dto.TaskRequest;
import com.kajtekh.jirabackend.model.task.dto.TaskResponse;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.repository.TaskRepository;
import com.kajtekh.jirabackend.repository.TaskTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskTypeRepository taskTypeRepository;


    public TaskService(final TaskRepository taskRepository, final TaskTypeRepository taskTypeRepository) {
        this.taskRepository = taskRepository;
        this.taskTypeRepository = taskTypeRepository;
    }

    @Transactional
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream().map(TaskResponse::fromTask).toList();
    }

    @Transactional
    public Task addTask(final TaskRequest taskRequest, final Issue issue, final User assignee) {
        final var task = new Task();
        taskTypeRepository.findByName(taskRequest.taskType()).ifPresentOrElse(
                taskType -> {
                    task.setName(taskRequest.name());
                    task.setStatus(Status.OPEN);
                    task.setDescription(taskRequest.description());
                    task.setTaskType(taskType);
                    task.setAssignee(assignee);
                    task.setIssue(issue);
                    task.setCreatedAt(LocalDateTime.now().truncatedTo(MINUTES));
                    task.setUpdatedAt(LocalDateTime.now().truncatedTo(MINUTES));
                },
                () -> {
                    throw new IllegalArgumentException("Task type not found");
                }
        );
        return taskRepository.save(task);
    }

    public Task moveTask(final Long id, final Status taskStatus) {
        final var task = taskRepository.findById(id).orElseThrow();
        task.setStatus(taskStatus);
        task.setUpdatedAt(LocalDateTime.now().truncatedTo(MINUTES));
        taskRepository.save(task);
        return task;
    }

    public void deleteTask(final Long id) {
        taskRepository.deleteById(id);
    }

    public List<TaskResponse> getTasksByStatus(final Status status, final Long issueId) {
        return taskRepository.findAllByStatusAndIssueId(status, issueId).stream().map(TaskResponse::fromTask).toList();
    }

    public Task updateTask(final Long id, final TaskRequest taskRequest, final User assignee) {
        final var task = taskRepository.findById(id).orElseThrow();
        final var taskType = taskTypeRepository.findByName(taskRequest.taskType()).orElseThrow();
        task.setName(taskRequest.name());
        task.setDescription(taskRequest.description());
        task.setTaskType(taskType);
        task.setAssignee(assignee);
        task.setUpdatedAt(LocalDateTime.now().truncatedTo(MINUTES));
        taskRepository.save(task);
        return task;
    }
}
