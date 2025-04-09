package com.kajtekh.jirabackend.service;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class CacheService {
    private static final Logger LOG = LoggerFactory.getLogger(CacheService.class);

    @Caching(evict = {
            @CacheEvict(value = "tasksByStatus", key = "T(String).valueOf(#id) + #newStatus.name()"),
            @CacheEvict(value = "tasksByStatus", key = "T(String).valueOf(#id) + #oldStatus.name()")
    })
    public void evictCacheOnMoveTask(final Long id, final Status oldStatus, final Status newStatus) {
    }
}
