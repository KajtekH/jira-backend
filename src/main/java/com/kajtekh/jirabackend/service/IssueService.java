package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.issue.Issue;
import com.kajtekh.jirabackend.model.issue.dto.IssueRequest;
import com.kajtekh.jirabackend.model.issue.dto.IssueResponse;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.repository.IssueRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

import static com.kajtekh.jirabackend.model.Status.ABANDONED;
import static com.kajtekh.jirabackend.model.Status.CLOSED;
import static com.kajtekh.jirabackend.model.Status.OPEN;

@Service
public class IssueService {

    private final IssueRepository issueRepository;


    public IssueService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public Issue addIssue(IssueRequest issueRequest, User productManager) {
        final var issue = new Issue();
        issue.setName(issueRequest.name());
        issue.setDescription(issueRequest.description());
        issue.setOpenDate(LocalDate.now());
        issue.setStatus(OPEN);
        issue.setProductManager(productManager);
        return issueRepository.save(issue);
    }

    public Issue getIssueById(Long id) {
        return issueRepository.findById(id).orElse(null);
    }

    public List<IssueResponse> getAllIssues() {
        return issueRepository.findAll().stream()
                .sorted((issue1, issue2) -> {
                    List<Status> order = List.of(OPEN, CLOSED, ABANDONED);
                    return Integer.compare(order.indexOf(issue1.getStatus()), order.indexOf(issue2.getStatus()));
                })
                .map(IssueResponse::fromIssue)
                .toList();
    }

    public Issue updateStatus(Long id, Status status) {
        final var issue = issueRepository.findById(id).orElseThrow();
        issue.setStatus(status);
        if (status.equals(CLOSED)) {
            issue.setCloseDate(LocalDate.now());
        }
        return issueRepository.save(issue);
    }
}
