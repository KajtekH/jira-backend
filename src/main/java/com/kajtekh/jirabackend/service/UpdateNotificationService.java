package com.kajtekh.jirabackend.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class UpdateNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public UpdateNotificationService(final SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyTaskListUpdate(final Long taskListId) {
        messagingTemplate.convertAndSend("/tasks", taskListId);
    }

    public void notifyIssueListUpdate(final Long issueListId) {
        messagingTemplate.convertAndSend("/issues", issueListId);
    }

    public void notifyRequestListUpdate(final Long requestListId) {
        messagingTemplate.convertAndSend("/requests", requestListId);
    }

    public void notifyProductListUpdate() {
        messagingTemplate.convertAndSend("/products", "");
    }

    public void notifyUserListUpdate() {
        messagingTemplate.convertAndSend("/users", "");
    }
}
