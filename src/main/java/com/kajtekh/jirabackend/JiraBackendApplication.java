package com.kajtekh.jirabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class JiraBackendApplication {

    public static void main(final String[] args) {
        SpringApplication.run(JiraBackendApplication.class, args);
    }

}
