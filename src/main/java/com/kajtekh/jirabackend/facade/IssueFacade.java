package com.kajtekh.jirabackend.facade;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.issue.dto.IssueRequest;
import com.kajtekh.jirabackend.model.issue.dto.IssueResponse;
import com.kajtekh.jirabackend.service.IssueService;
import com.kajtekh.jirabackend.service.RequestService;
import com.kajtekh.jirabackend.service.UpdateNotificationService;
import com.kajtekh.jirabackend.service.UserService;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueFacade {

    private final IssueService issueService;
    private final UserService userService;
    private final RequestService requestService;
    private final UpdateNotificationService updateNotificationService;
    private final Cache cache;

    public IssueFacade(final IssueService issueService, final UserService userService, final RequestService requestService,
                       final UpdateNotificationService updateNotificationService, final Cache cache) {
        this.issueService = issueService;
        this.userService = userService;
        this.requestService = requestService;
        this.updateNotificationService = updateNotificationService;
        this.cache = cache;
    }

    @Cacheable(value = "data", key = "'issues' + #id")
    public List<IssueResponse> getAllIssues(final Long id) {
        return issueService.getAllIssues(id);
    }

    @Cacheable(value = "data", key = "'issue' + #id")
    public IssueResponse getIssueById(final Long id) {
        return IssueResponse.fromIssue(issueService.getIssueById(id));
    }

    public IssueResponse updateStatus(final Long id, final Status status) {
       final var issue = issueService.updateStatus(id, status);
        cache.evictIfPresent("issues" + issue.getRequest().getId());
        cache.put("issue" + issue.getId(), IssueResponse.fromIssue(issue));
        updateNotificationService.notifyIssueListUpdate(issue.getRequest().getId());
        return IssueResponse.fromIssue(issue);
    }

    public IssueResponse addIssue(final IssueRequest issueRequest, final Long requestId) {
        final var request = requestService.getRequestById(requestId);
        final var productManager = userService.getUserByUsername(issueRequest.productManager());
        final var issue = issueService.addIssue(issueRequest, productManager, request);
        cache.evictIfPresent("issues" + requestId);
        cache.put("issue" + issue.getId(), IssueResponse.fromIssue(issue));
        updateNotificationService.notifyIssueListUpdate(requestId);
        return IssueResponse.fromIssue(issue);
    }

}
