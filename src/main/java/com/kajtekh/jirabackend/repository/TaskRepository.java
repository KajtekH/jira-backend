package com.kajtekh.jirabackend.repository;

import com.kajtekh.jirabackend.model.task.Status;
import com.kajtekh.jirabackend.model.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(Status status);
}
