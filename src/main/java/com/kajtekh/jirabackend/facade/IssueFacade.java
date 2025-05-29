package com.kajtekh.jirabackend.facade;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.issue.dto.IssueRequest;
import com.kajtekh.jirabackend.model.issue.dto.IssueResponse;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.service.IssueService;
import com.kajtekh.jirabackend.service.RequestService;
import com.kajtekh.jirabackend.service.UpdateNotificationService;
import com.kajtekh.jirabackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueFacade {

    private static final Logger LOG = LoggerFactory.getLogger(IssueFacade.class);
    private static final String CACHE_EVICTED_MSG = "Cache evicted for key: issues{}";

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
        LOG.debug("Fetching all issues for request with ID: {}", id);
        return issueService.getAllIssues(id);
    }

    @Cacheable(value = "data", key = "'issue' + #id")
    public IssueResponse getIssueById(final Long id) {
        LOG.debug("Fetching issue with ID: {}", id);
        return IssueResponse.fromIssue(issueService.getIssueById(id));
    }

    public IssueResponse updateStatus(final Long id, final Status status, final String result) {
        LOG.debug("Updating issue with ID: {} to status: {}", id, status);
        final var issue = issueService.updateStatus(id, status, result);
        cache.evictIfPresent("issues" + issue.getRequest().getId());
        cache.put("issue" + issue.getId(), IssueResponse.fromIssue(issue));
        LOG.trace(CACHE_EVICTED_MSG, issue.getRequest().getId());
        updateNotificationService.notifyIssueListUpdate(issue.getRequest().getId());
        return IssueResponse.fromIssue(issue);
    }

    public IssueResponse addIssue(final IssueRequest issueRequest, final Long requestId) {
        LOG.debug("Adding issue for requestId: {} with request: {}", requestId, issueRequest);
        final var request = requestService.getRequestById(requestId);
        final var productManager = userService.getProductManager(issueRequest.productManager());
        final var issue = issueService.addIssue(issueRequest, productManager, request);
        cache.evictIfPresent("issues" + requestId);
        cache.put("issue" + issue.getId(), IssueResponse.fromIssue(issue));
        LOG.trace(CACHE_EVICTED_MSG, requestId);
        updateNotificationService.notifyIssueListUpdate(requestId);
        return IssueResponse.fromIssue(issue);
    }

    public boolean isAssignedToRequest(final Long issueId, final Authentication authentication) {
        final var issue = issueService.getIssueById(issueId);
        final var request = issue.getRequest();
        final var user = (User) authentication.getPrincipal();
        return request.getAccountManager().getId().equals(user.getId());
    }

    public boolean isAssignedToIssue(final Long issueId, final Authentication authentication) {
        final var issue = issueService.getIssueById(issueId);
        final var user = (User) authentication.getPrincipal();
        return issue.getProductManager().getId().equals(user.getId());
    }
}
