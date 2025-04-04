package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.issue.Issue;
import com.kajtekh.jirabackend.model.task.dto.TaskResponse;
import com.kajtekh.jirabackend.model.task.Task;
import com.kajtekh.jirabackend.model.task.dto.TaskRequest;
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


    public TaskService(TaskRepository taskRepository, TaskTypeRepository taskTypeRepository) {
        this.taskRepository = taskRepository;
        this.taskTypeRepository = taskTypeRepository;
    }

    @Transactional
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream().map(TaskResponse::fromTask).toList();
    }

    @Transactional
    public Task addTask(TaskRequest taskRequest, Issue issue, User assignee) {
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

    public Task moveTask(Long id, Status taskStatus) {
        final var task = taskRepository.findById(id).orElseThrow();
        task.setStatus(taskStatus);
        task.setUpdatedAt(LocalDateTime.now().truncatedTo(MINUTES));
        taskRepository.save(task);
        return task;
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public List<TaskResponse> getTasksByStatus(Status status, Long issueId) {
        return taskRepository.findAllByStatusAndIssueId(status, issueId).stream().map(TaskResponse::fromTask).toList();
    }

    public Task updateTask(Long id, TaskRequest taskRequest, User assignee) {
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
