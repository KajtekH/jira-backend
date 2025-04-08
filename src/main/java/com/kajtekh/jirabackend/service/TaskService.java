package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.issue.Issue;
import com.kajtekh.jirabackend.model.task.Task;
import com.kajtekh.jirabackend.model.task.dto.TaskRequest;
import com.kajtekh.jirabackend.model.task.dto.TaskResponse;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.repository.TaskRepository;
import com.kajtekh.jirabackend.repository.TaskTypeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskTypeRepository taskTypeRepository;
    private final CacheService cacheService;


    public TaskService(final TaskRepository taskRepository, final TaskTypeRepository taskTypeRepository, final CacheService cacheService) {
        this.taskRepository = taskRepository;
        this.taskTypeRepository = taskTypeRepository;
        this.cacheService = cacheService;
    }

    @Transactional(readOnly = true)
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
                    task.setPriority(taskRequest.priority());
                },
                () -> {
                    throw new IllegalArgumentException("Task type not found");
                }
        );
        return taskRepository.save(task);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "tasksByStatus", key = "T(String).valueOf(#task.issue.id) + #task.status.name()"),
            @CacheEvict(value = "tasksByStatus", key = "T(String).valueOf(#task.issue.id) + #oldStatus.name()")
    })
    public Task moveTask(final Long id, final Status taskStatus) {
        final var task = taskRepository.findById(id).orElseThrow();
        final var oldStatus = task.getStatus();
        task.setStatus(taskStatus);
        task.setUpdatedAt(LocalDateTime.now().truncatedTo(MINUTES));
        taskRepository.save(task);
        cacheService.evictCacheOnMoveTask(task, oldStatus);
        return task;
    }

    @Transactional
    public void deleteTask(final Long id) {
        taskRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByStatus(final Status status, final Long issueId) {
        return taskRepository.findAllByStatusAndIssueId(status, issueId).stream().map(TaskResponse::fromTask).toList();
    }

    @Transactional
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
