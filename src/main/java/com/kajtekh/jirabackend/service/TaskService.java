package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.issue.Issue;
import com.kajtekh.jirabackend.model.task.dto.TaskResponse;
import com.kajtekh.jirabackend.model.task.TaskStatus;
import com.kajtekh.jirabackend.model.task.Task;
import com.kajtekh.jirabackend.model.task.dto.TaskRequest;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.kajtekh.jirabackend.model.task.TaskStatus.TO_DO;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class TaskService {

    private final TaskRepository taskRepository;


    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream().map(TaskResponse::fromTask).toList();
    }

    @Transactional
    public Task addTask(TaskRequest taskRequest, Issue issue, User assignee) {
        final var task = new Task();
        task.setName(taskRequest.name());
        task.setTaskStatus(TO_DO);
        task.setDescription(taskRequest.description());
        task.setTaskType(taskRequest.taskType());
        task.setAssignee(assignee);
        task.setIssue(issue);
        task.setCreatedAt(now().truncatedTo(MINUTES));
        task.setUpdatedAt(now().truncatedTo(MINUTES));
        return taskRepository.save(task);
    }

    public Task moveTask(Long id, TaskStatus taskStatus) {
        final var task = taskRepository.findById(id).orElseThrow();
        task.setTaskStatus(taskStatus);
        task.setUpdatedAt(now().truncatedTo(MINUTES));
        taskRepository.save(task);
        return task;
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public List<TaskResponse> getTasksByStatus(TaskStatus taskStatus, Long issueId) {
        return taskRepository.findAllByTaskStatusAndIssueId(taskStatus, issueId).stream().map(TaskResponse::fromTask).toList();
    }

    public Task updateTask(Long id, TaskRequest taskRequest, User assignee) {
        final var task = taskRepository.findById(id).orElseThrow();
        task.setName(taskRequest.name());
        task.setDescription(taskRequest.description());
        task.setTaskType(taskRequest.taskType());
        task.setAssignee(assignee);
        task.setUpdatedAt(now().truncatedTo(MINUTES));
        taskRepository.save(task);
        return task;
    }
}
