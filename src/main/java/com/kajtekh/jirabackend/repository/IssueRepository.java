package com.kajtekh.jirabackend.repository;

import com.kajtekh.jirabackend.model.issue.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findAllByRequestId(Long requestId);
}
