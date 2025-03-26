package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.issue.Issue;
import com.kajtekh.jirabackend.model.task.dto.TaskResponse;
import com.kajtekh.jirabackend.model.task.TaskStatus;
import com.kajtekh.jirabackend.model.task.Task;
import com.kajtekh.jirabackend.model.task.dto.TaskRequest;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.repository.IssueRepository;
import com.kajtekh.jirabackend.repository.TaskRepository;
import com.kajtekh.jirabackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream().map(TaskResponse::fromTask).toList();
    }

    public Task addTask(TaskRequest taskRequest, Issue issue, User assignee) {
        final var task = new Task();
        task.setName(taskRequest.name());
        task.setTaskStatus(TaskStatus.TO_DO);
        task.setDescription(taskRequest.description());
        task.setType(taskRequest.type());
        task.setAssignee(assignee);
        task.setIssue(issue);
        task.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        task.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        return taskRepository.save(task);
    }

    public Task moveTask(Long id, TaskStatus taskStatus) {
        final var task = taskRepository.findById(id).orElseThrow();
        task.setTaskStatus(taskStatus);
        task.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
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
        task.setType(taskRequest.type());
        task.setAssignee(assignee);
        task.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        taskRepository.save(task);
        return task;
    }
}
