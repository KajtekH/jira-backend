package com.kajtekh.jirabackend.model.task;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.issue.Issue;
import com.kajtekh.jirabackend.model.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Status status;
    private int priority;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    private TaskType taskType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @Override
    public String toString() {
            return "Task{" +
                    " name='" + name + '\'' +
                    ", status=" + status +
                    ", priority=" + priority +
                    ", description='" + description + '\'' +
                    ", createdAt=" + createdAt +
                    ", updatedAt=" + updatedAt +
                    ", taskType=" + taskType.getName() +
                    ", issue=" + issue.getName() +
                    ", assignee=" + assignee.getUsername() +
                    '}';
    }
}
