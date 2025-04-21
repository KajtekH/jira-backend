package com.kajtekh.jirabackend.repository;

import com.kajtekh.jirabackend.model.issue.IssueType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IssueTypeRepository extends JpaRepository<IssueType, Long> {
    Optional<IssueType> findByName(String name);
}
