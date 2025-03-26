package com.kajtekh.jirabackend.repository;

import com.kajtekh.jirabackend.model.task.TaskStatus;
import com.kajtekh.jirabackend.model.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByTaskStatus(TaskStatus taskStatus);
    List<Task> findAllByTaskStatusAndIssueId(TaskStatus taskStatus, Long issueId);
}
