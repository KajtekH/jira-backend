package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.exception.TaskNotFoundException;
import com.kajtekh.jirabackend.exception.TaskTypeNotFoundException;
import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.issue.Issue;
import com.kajtekh.jirabackend.model.task.Task;
import com.kajtekh.jirabackend.model.task.dto.MoveTaskRequest;
import com.kajtekh.jirabackend.model.task.dto.TaskListResponse;
import com.kajtekh.jirabackend.model.task.dto.TaskRequest;
import com.kajtekh.jirabackend.model.task.dto.TaskResponse;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.repository.TaskRepository;
import com.kajtekh.jirabackend.repository.TaskTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MINUTES;


@Service
public class TaskService {
    private static final Logger LOG = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final TaskTypeRepository taskTypeRepository;

    public TaskService(final TaskRepository taskRepository, final TaskTypeRepository taskTypeRepository) {
        this.taskRepository = taskRepository;
        this.taskTypeRepository = taskTypeRepository;
    }

    @Transactional
    public Task addTask(final TaskRequest taskRequest, final Issue issue, final Optional<User> assignee) {
        final var task = new Task();
        taskTypeRepository.findByName(taskRequest.type()).ifPresentOrElse(
                taskType -> {
                    task.setName(taskRequest.name());
                    task.setStatus(Status.OPEN);
                    task.setDescription(taskRequest.description());
                    task.setTaskType(taskType);
                    assignee.ifPresent(task::setAssignee);
                    task.setIssue(issue);
                    task.setCreatedAt(LocalDateTime.now().truncatedTo(MINUTES));
                    task.setUpdatedAt(LocalDateTime.now().truncatedTo(MINUTES));
                    task.setPriority(taskRequest.priority());
                    taskRepository.save(task);
                    LOG.info("Task added successfully: '{}'", task);
                },
                () -> {
                    LOG.warn("Task type not found: '{}'", taskRequest.type());
                    throw new TaskTypeNotFoundException("Task type not found");
                }
        );
        return task;
    }

    @Transactional
    public Task moveTask(final MoveTaskRequest moveTaskRequest) {
        final var task = taskRepository.findById(moveTaskRequest.taskId()).orElseThrow(() -> {
            LOG.warn("Task with ID: '{}' not found ", moveTaskRequest.taskId());
            return new TaskNotFoundException("Task not found");
        });
        final var oldStatus = task.getStatus();
        task.setStatus(moveTaskRequest.status());
        task.setResult(moveTaskRequest.result());
        task.setUpdatedAt(LocalDateTime.now().truncatedTo(MINUTES));
        taskRepository.save(task);
        LOG.info("Task with ID '{}' moved from '{}' to '{}'", moveTaskRequest.taskId(), oldStatus, moveTaskRequest.status());
        LOG.debug("Task details after move: {}", task);
        return task;
    }

    @Transactional(readOnly = true)
    public TaskListResponse getAllTasksByIssue(final Long issueId) {
        final var tasks = taskRepository.findAllByIssueId(issueId).stream().map(TaskResponse::fromTask).toList();
        final var inProgressTasks = tasks.stream()
                .filter(task -> task.status().equals(Status.IN_PROGRESS.name()))
                .toList();
        final var openTasks = tasks.stream()
                .filter(task -> task.status().equals(Status.OPEN.name()))
                .toList();
        final var closedTasks = tasks.stream()
                .filter(task -> task.status().equals(Status.CLOSED.name()))
                .toList();
        final var abandonedTasks = tasks.stream()
                .filter(task -> task.status().equals(Status.ABANDONED.name()))
                .toList();
        LOG.debug("Tasks fetched for issue ID: '{}' - Open: {}, In Progress: {}, Closed: {}, Abandoned: {}",
                issueId, openTasks.size(), inProgressTasks.size(), closedTasks.size(), abandonedTasks.size());
        return new TaskListResponse(
                openTasks,
                inProgressTasks,
                closedTasks,
                abandonedTasks
        );
    }

    @Transactional
    public Task updateTask(final Long id, final TaskRequest taskRequest, final Optional<User> assignee) {
        final var task = taskRepository.findById(id).orElseThrow(() -> {
            LOG.warn("Task with ID: '{}' not found", id);
            return new TaskNotFoundException("Task not found");
        });
        final var taskType = taskTypeRepository.findByName(taskRequest.type()).orElseThrow(() -> {
            LOG.warn("Task type not found: '{}'", taskRequest.type());
            return new TaskTypeNotFoundException("Task type not found");
        });
        task.setName(taskRequest.name());
        task.setDescription(taskRequest.description());
        task.setTaskType(taskType);
        assignee.ifPresent(task::setAssignee);
        task.setUpdatedAt(LocalDateTime.now().truncatedTo(MINUTES));
        taskRepository.save(task);
        LOG.info("Task with ID '{}' updated successfully", id);
        return task;
    }

    @Transactional(readOnly = true)
    public Task  getTaskById(final Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> {
            LOG.warn("Task with ID: '{}' not found ", taskId);
            return new TaskNotFoundException("Task not found");
        });
    }
}
