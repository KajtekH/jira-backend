package com.kajtekh.jirabackend.model.issue.dto;


public record IssueRequest(String name, String description, String productManager) {
}
