package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.task.Status;
import com.kajtekh.jirabackend.model.task.Task;
import com.kajtekh.jirabackend.model.task.TaskRequest;
import com.kajtekh.jirabackend.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTaskByStatus(Status status) {
        return taskRepository.findByStatus(status);
    }

    public Task addTask(TaskRequest taskRequest) {
        final var task = new Task(
                taskRequest.name(),
                taskRequest.description(),
                taskRequest.assignee(),
                taskRequest.status(),
                taskRequest.type(),
                LocalDateTime.now()
        );
        taskRepository.save(task);
        return task;
    }

    public Task moveTask(Long id, Status status) {
        final var task = taskRepository.findById(id).orElseThrow();
        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);
        return task;
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
