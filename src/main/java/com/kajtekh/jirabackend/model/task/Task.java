package com.kajtekh.jirabackend.model.task;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String assignee;
    private Status status;
    private Type type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Task(String name, String description, String assignee, Status status, Type type, LocalDateTime now) {
        this.name = name;
        this.description = description;
        this.assignee = assignee;
        this.status = status;
        this.type = type;
        this.createdAt = now.truncatedTo(ChronoUnit.MINUTES);
        this.updatedAt = now.truncatedTo(ChronoUnit.MINUTES);
    }
}
