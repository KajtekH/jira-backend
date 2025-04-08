package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.task.Task;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Caching(evict = {
            @CacheEvict(value = "tasksByStatus", key = "T(String).valueOf(#task.issue.id) + #task.status.name()"),
            @CacheEvict(value = "tasksByStatus", key = "T(String).valueOf(#task.issue.id) + #oldStatus.name()")
    })
    public void evictCacheOnMoveTask(final Task task, final Status oldStatus) {}
}
