package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.issue.Issue;
import com.kajtekh.jirabackend.model.task.Task;
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
import java.util.List;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;

@Service
public class TaskService {
    private static final Logger LOG = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final TaskTypeRepository taskTypeRepository;

    public TaskService(final TaskRepository taskRepository, final TaskTypeRepository taskTypeRepository) {
        this.taskRepository = taskRepository;
        this.taskTypeRepository = taskTypeRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        LOG.info("Fetching all tasks");
        return taskRepository.findAll().stream().map(TaskResponse::fromTask).toList();
    }

    @Transactional
    public Task addTask(final TaskRequest taskRequest, final Issue issue, final User assignee) {
        LOG.info("Adding a new task with name: {}", taskRequest.name());
        final var task = new Task();
        taskTypeRepository.findByName(taskRequest.type()).ifPresentOrElse(
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
                    LOG.error("Task type not found: {}", taskRequest.type());
                    throw new IllegalArgumentException("Task type not found");
                }
        );
        LOG.info("Task added successfully: {}", task.getName());
        return taskRepository.save(task);
    }

    @Transactional
    public Task moveTask(final Long id, final Status taskStatus) {
        LOG.info("Moving task with ID: {} to status: {}", id, taskStatus);
        final var task = taskRepository.findById(id).orElseThrow(() -> {
            LOG.error("Task not found with ID: {}", id);
            return new IllegalArgumentException("Task not found");
        });
        final var oldStatus = task.getStatus();
        task.setStatus(taskStatus);
        task.setUpdatedAt(LocalDateTime.now().truncatedTo(MINUTES));
        taskRepository.save(task);
        LOG.info("Task with ID: {} moved from {} to {}", id, oldStatus, taskStatus);
        return task;
    }

    @Transactional
    public void deleteTask(final Long id) {
        LOG.info("Deleting task with ID: {}", id);
        taskRepository.deleteById(id);
        LOG.info("Task with ID: {} deleted successfully", id);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByStatus(final Status status, final Long issueId) {
        LOG.info("Fetching tasks with status: {} for issue ID: {}", status, issueId);
        return taskRepository.findAllByStatusAndIssueId(status, issueId).stream().map(TaskResponse::fromTask).toList();
    }

    @Transactional(readOnly = true)
    public TaskListResponse getAllTasksByIssue(final Long issueId) {
        LOG.info("Fetching all tasks for issue ID: {}", issueId);
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
        LOG.info("Tasks fetched for issue ID: {} - Open: {}, In Progress: {}, Closed: {}, Abandoned: {}",
                issueId, openTasks.size(), inProgressTasks.size(), closedTasks.size(), abandonedTasks.size());
        return new TaskListResponse(
                openTasks,
                inProgressTasks,
                closedTasks,
                abandonedTasks
        );
    }

    @Transactional
    public Task updateTask(final Long id, final TaskRequest taskRequest, final User assignee) {
        LOG.info("Updating task with ID: {}", id);
        final var task = taskRepository.findById(id).orElseThrow(() -> {
            LOG.error("Task not found with ID: {}", id);
            return new IllegalArgumentException("Task not found");
        });
        final var taskType = taskTypeRepository.findByName(taskRequest.type()).orElseThrow(() -> {
            LOG.error("Task type not found: {}", taskRequest.type());
            return new IllegalArgumentException("Task type not found");
        });
        task.setName(taskRequest.name());
        task.setDescription(taskRequest.description());
        task.setTaskType(taskType);
        task.setAssignee(assignee);
        task.setUpdatedAt(LocalDateTime.now().truncatedTo(MINUTES));
        taskRepository.save(task);
        LOG.info("Task with ID: {} updated successfully", id);
        return task;
    }
}
