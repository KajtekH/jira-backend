package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.task.dto.TaskResponse;
import com.kajtekh.jirabackend.model.task.TaskStatus;
import com.kajtekh.jirabackend.model.task.Task;
import com.kajtekh.jirabackend.model.task.dto.TaskRequest;
import com.kajtekh.jirabackend.repository.TaskRepository;
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

    public Task addTask(TaskRequest taskRequest) {
        final var task = new Task();
        task.setName(taskRequest.name());
        task.setTaskStatus(TaskStatus.TO_DO);
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

    public List<TaskResponse> getTasksByStatus(TaskStatus taskStatus) {
        return taskRepository.findByTaskStatus(taskStatus).stream().map(TaskResponse::fromTask).toList();
    }
}
