package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.issue.Issue;
import com.kajtekh.jirabackend.model.issue.dto.IssueRequest;
import com.kajtekh.jirabackend.model.issue.dto.IssueResponse;
import com.kajtekh.jirabackend.model.request.Request;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.repository.IssueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.kajtekh.jirabackend.model.Status.ABANDONED;
import static com.kajtekh.jirabackend.model.Status.CLOSED;
import static com.kajtekh.jirabackend.model.Status.IN_PROGRESS;
import static com.kajtekh.jirabackend.model.Status.OPEN;
import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class IssueService {

    private final IssueRepository issueRepository;


    public IssueService(final IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    @Transactional
    public Issue addIssue(final IssueRequest issueRequest, final User productManager, final Request request) {
        final var issue = new Issue();
        issue.setName(issueRequest.name());
        issue.setDescription(issueRequest.description());
        issue.setOpenDate(LocalDateTime.now().truncatedTo(MINUTES));
        issue.setStatus(OPEN);
        issue.setProductManager(productManager);
        issue.setRequest(request);
        return issueRepository.save(issue);
    }

    @Transactional(readOnly = true)
    public Issue getIssueById(final Long id) {
        return issueRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> getAllIssues(final Long id) {
        return issueRepository.findAllByRequestId(id).stream()
                .sorted((issue1, issue2) -> {
                    final List<Status> order = List.of(OPEN,IN_PROGRESS, CLOSED, ABANDONED);
                    return Integer.compare(order.indexOf(issue1.getStatus()), order.indexOf(issue2.getStatus()));
                })
                .map(IssueResponse::fromIssue)
                .toList();
    }

    @Transactional
    public Issue updateStatus(final Long id, final Status status) {
        final var issue = issueRepository.findById(id).orElseThrow();
        issue.setStatus(status);
        if (status.equals(CLOSED)) {
            issue.setCloseDate(LocalDateTime.now().truncatedTo(MINUTES));
        }
        return issueRepository.save(issue);
    }
}
