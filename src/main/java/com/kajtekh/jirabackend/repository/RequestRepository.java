package com.kajtekh.jirabackend.repository;

import com.kajtekh.jirabackend.model.Status;
import com.kajtekh.jirabackend.model.request.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByStatus(Status status);
    List<Request> findAllByProductId(Long productId);
}
