package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.exception.IssueNotFoundException;
import com.kajtekh.jirabackend.exception.IssueTypeNotFoundException;
import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.issue.Issue;
import com.kajtekh.jirabackend.model.issue.dto.IssueRequest;
import com.kajtekh.jirabackend.model.issue.dto.IssueResponse;
import com.kajtekh.jirabackend.model.request.Request;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.repository.IssueRepository;
import com.kajtekh.jirabackend.repository.IssueTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(IssueService.class);

    private final IssueRepository issueRepository;
    private final IssueTypeRepository issueTypeRepository;

    public IssueService(final IssueRepository issueRepository, final IssueTypeRepository issueTypeRepository) {
        this.issueRepository = issueRepository;
        this.issueTypeRepository = issueTypeRepository;
    }

    @Transactional
    public Issue addIssue(final IssueRequest issueRequest, final User productManager, final Request request) {
        final var issue = new Issue();
        issueTypeRepository.findByName(issueRequest.issueType()).ifPresentOrElse(
                issueType -> {
                    issue.setName(issueRequest.name());
                    issue.setDescription(issueRequest.description());
                    issue.setIssueType(issueType);
                    issue.setProductManager(productManager);
                    issue.setRequest(request);
                    issue.setStatus(OPEN);
                    issue.setOpenDate(LocalDateTime.now().truncatedTo(MINUTES));
                    issueRepository.save(issue);
                    LOG.info("Issue added successfully: {}", issue);
                },
                () -> {
                    LOG.warn("Issue type not found: {}", issueRequest.issueType());
                    throw new IssueTypeNotFoundException("Issue type not found: ");
                }
        );
        return issue;
    }

    @Transactional(readOnly = true)
    public Issue getIssueById(final Long id) {
        return issueRepository.findById(id).orElseThrow(() -> {
            LOG.warn("Issue with ID: '{}' not found ", id);
            return new IssueNotFoundException("Issue not found");
        });
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
    public Issue updateStatus(final Long id, final Status status, final String result) {
        final var issue = issueRepository.findById(id).orElseThrow(() -> {
            LOG.warn("Issue with ID: '{}' not found ", id);
            return new IssueNotFoundException("Issue not found");
        });
        issue.setStatus(status);
        if (status.equals(CLOSED)) {
            issue.setCloseDate(LocalDateTime.now().truncatedTo(MINUTES));
        }
        issue.setResult(result);
        issueRepository.save(issue);
        LOG.info("Issue with ID: {} updated to status: {} and result {}", id, status, result);
        return issue;
    }
}
