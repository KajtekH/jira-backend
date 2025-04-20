package com.kajtekh.jirabackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class UpdateNotificationService {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateNotificationService.class);

    private final SimpMessagingTemplate messagingTemplate;

    public UpdateNotificationService(final SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyTaskListUpdate(final Long taskListId) {
        messagingTemplate.convertAndSend("/tasks", taskListId);
        LOG.info("Notification sent for task list update with ID: {}", taskListId);
    }

    public void notifyIssueListUpdate(final Long issueListId) {
        messagingTemplate.convertAndSend("/issues", issueListId);
        LOG.info("Notification sent for issue list update with ID: {}", issueListId);
    }

    public void notifyRequestListUpdate(final Long requestListId) {
        messagingTemplate.convertAndSend("/requests", requestListId);
        LOG.info("Notification sent for request list update with ID: {}", requestListId);
    }

    public void notifyProductListUpdate() {
        messagingTemplate.convertAndSend("/products", "");
        LOG.info("Notification sent for product list update");
    }

    public void notifyUserListUpdate() {
        messagingTemplate.convertAndSend("/users", "");
        LOG.info("Notification sent for user list update");
    }
}
