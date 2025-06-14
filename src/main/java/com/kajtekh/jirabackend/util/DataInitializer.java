package com.kajtekh.jirabackend.util;

import com.kajtekh.jirabackend.model.issue.Issue;
import com.kajtekh.jirabackend.model.issue.IssueType;
import com.kajtekh.jirabackend.model.product.Product;
import com.kajtekh.jirabackend.model.request.Request;
import com.kajtekh.jirabackend.model.task.Task;
import com.kajtekh.jirabackend.model.task.TaskType;
import com.kajtekh.jirabackend.model.user.User;
import com.kajtekh.jirabackend.repository.IssueRepository;
import com.kajtekh.jirabackend.repository.IssueTypeRepository;
import com.kajtekh.jirabackend.repository.ProductRepository;
import com.kajtekh.jirabackend.repository.RequestRepository;
import com.kajtekh.jirabackend.repository.TaskRepository;
import com.kajtekh.jirabackend.repository.TaskTypeRepository;
import com.kajtekh.jirabackend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.kajtekh.jirabackend.model.Status.OPEN;
import static com.kajtekh.jirabackend.model.request.RequestType.MAJOR;
import static com.kajtekh.jirabackend.model.request.RequestType.MINOR;
import static com.kajtekh.jirabackend.model.request.RequestType.PATCH;
import static com.kajtekh.jirabackend.model.user.Role.ACCOUNT_MANAGER;
import static com.kajtekh.jirabackend.model.user.Role.ADMIN;
import static com.kajtekh.jirabackend.model.user.Role.OWNER;
import static com.kajtekh.jirabackend.model.user.Role.PRODUCT_MANAGER;
import static com.kajtekh.jirabackend.model.user.Role.USER;
import static com.kajtekh.jirabackend.model.user.Role.WORKER;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;


@Configuration
@AllArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initGodUser(final UserRepository userRepository) {
        if (userRepository.findByUsername("tab_admin").isPresent()) {
            return args -> {};
        }
        return args -> {
            final var godUser = User.builder()
                    .username("tab_admin")
                    .email("tabadmin@gmail.com")
                    .firstName("Tab")
                    .lastName("Admin")
                    .role(ADMIN)
                    .password(passwordEncoder.encode("admin"))
                    .isActive(true)
                    .build();
            userRepository.save(godUser);
        };
    }

    @Bean
    @Profile("test")
    CommandLineRunner initTestData(final UserRepository userRepository,
                                   final TaskTypeRepository taskTypeRepository,
                                   final TaskRepository taskRepository,
                                   final IssueTypeRepository issueTypeRepository,
                                   final IssueRepository issueRepository,
                                   final RequestRepository requestRepository,
                                   final ProductRepository productRepository) {

        if (isDatabaseEmpty(userRepository, taskTypeRepository, taskRepository,issueTypeRepository, issueRepository, requestRepository, productRepository)) {

            final var userList = prepareUsers();
            final var productList = prepareProducts(userList.get(4));
            final var requestList = prepareRequests(userList.get(3), productList);
            final var issueTypeList = prepareIssueTypes();
            final var issueList = prepareIssues(userList.get(2), requestList, issueTypeList);
            final var taskTypeList = prepareTaskTypes();
            final var taskList = prepareTasks(userList.get(1), issueList, taskTypeList);

            return args -> {
                userRepository.saveAll(userList);
                productRepository.saveAll(productList);
                requestRepository.saveAll(requestList);
                issueTypeRepository.saveAll(issueTypeList);
                issueRepository.saveAll(issueList);
                taskTypeRepository.saveAll(taskTypeList);
                taskRepository.saveAll(taskList);
            };
        }
        return args -> {};
    }

    private boolean isDatabaseEmpty(final UserRepository userRepository,
                                    final TaskTypeRepository taskTypeRepository,
                                    final TaskRepository taskRepository,
                                    final IssueTypeRepository issueTypeRepository,
                                    final IssueRepository issueRepository,
                                    final RequestRepository requestRepository,
                                    final ProductRepository productRepository) {
        return userRepository.count() < 2 &&
                taskTypeRepository.count() == 0 &&
                taskRepository.count() == 0 &&
                issueTypeRepository.count() == 0 &&
                issueRepository.count() == 0 &&
                requestRepository.count() == 0 &&
                productRepository.count() == 0;
    }

    private List<User> prepareUsers() {
        final List<User> userList = new ArrayList<>();
        userList.add(User.builder()
                .username("test_user")
                .firstName("Jan")
                .lastName("Kowalski")
                .email("test@gmail")
                .role(USER)
                .password(passwordEncoder.encode("test"))
                .isActive(true)
                .build());
        userList.add(User.builder()
                .username("test_worker")
                .firstName("Michał")
                .lastName("Nowak")
                .email("test2@gmail")
                .role(WORKER)
                .password(passwordEncoder.encode("test"))
                .isActive(true)
                .build());
        userList.add(User.builder()
                .username("test_product_manager")
                .firstName("Sławomir")
                .lastName("Nowakowski")
                .email("test3@gmail")
                .role(PRODUCT_MANAGER)
                .password(passwordEncoder.encode("test"))
                .isActive(true)
                .build());
        userList.add(User.builder()
                .username("test_account_manager")
                .firstName("Adrian")
                .lastName("Kowal")
                .email("test4@gmail")
                .role(ACCOUNT_MANAGER)
                .password(passwordEncoder.encode("test"))
                .isActive(true)
                .build());
        userList.add(User.builder()
                .username("test_owner")
                .firstName("Janusz")
                .lastName("Kowal")
                .email("test5@gmail")
                .role(OWNER)
                .password(passwordEncoder.encode("test"))
                .isActive(true)
                .build());
        return userList;
    }

    private List<Product> prepareProducts(final User user) {
        final List<Product> products = new ArrayList<>();
        products.add(Product.builder()
                .name("Test Product 1")
                .description("Description for Test Product 1")
                .version("0.0.0")
                .owner(user)
                .build());
        products.add(Product.builder()
                .name("Test Product 2")
                .description("Description for Test Product 2")
                .version("0.0.0")
                .owner(user)
                .build());
        return products;
    }

    private List<Request> prepareRequests(final User user, final List<Product> products) {
        final List<Request> requests = new ArrayList<>();
        requests.add(Request.builder()
                .name("Test Request 1")
                .description("Description for Test Request 1")
                .product(products.get(0))
                .accountManager(user)
                .status(OPEN)
                .requestType(MAJOR)
                .openDate(now().truncatedTo(MINUTES))
                .build());
        requests.add(Request.builder()
                .name("Test Request 2")
                .description("Description for Test Request 2")
                .product(products.get(1))
                .accountManager(user)
                .status(OPEN)
                .requestType(MAJOR)
                .openDate(now().truncatedTo(MINUTES))
                .build());
        requests.add(Request.builder()
                .name("Test Request 3")
                .description("Description for Test Request 3")
                .product(products.get(0))
                .accountManager(user)
                .status(OPEN)
                .requestType(MINOR)
                .openDate(now().truncatedTo(MINUTES))
                .build());
        requests.add(Request.builder()
                .name("Test Request 4")
                .description("Description for Test Request 4")
                .product(products.get(1))
                .accountManager(user)
                .status(OPEN)
                .requestType(PATCH)
                .openDate(now().truncatedTo(MINUTES))
                .build());
        return requests;
    }

    private List<IssueType> prepareIssueTypes() {
        final List<IssueType> issueTypes = new ArrayList<>();
        issueTypes.add(new IssueType("ERROR"));
        issueTypes.add(new IssueType("CRITICAL_ERROR"));
        issueTypes.add(new IssueType("CHANGE"));
        issueTypes.add(new IssueType("FEATURE"));
        return issueTypes;
    }

    private List<Issue> prepareIssues(final User user, final List<Request> requests, final List<IssueType> issueTypes) {
        final List<Issue> issues = new ArrayList<>();
        issues.add(Issue.builder()
                .name("Test Issue 1")
                .description("Description for Test Issue 1")
                .request(requests.get(0))
                .productManager(user)
                .openDate(now().truncatedTo(MINUTES))
                .status(OPEN)
                .issueType(issueTypes.get(0))
                .build());
        issues.add(Issue.builder()
                .name("Test Issue 2")
                .description("Description for Test Issue 2")
                .request(requests.get(1))
                .productManager(user)
                .openDate(now().truncatedTo(MINUTES))
                .status(OPEN)
                .issueType(issueTypes.get(1))
                .build());
        return issues;
    }

    private List<TaskType> prepareTaskTypes() {
        final List<TaskType> taskTypes = new ArrayList<>();
        taskTypes.add(new TaskType("QUALITY"));
        taskTypes.add(new TaskType("BUG"));
        taskTypes.add(new TaskType("FEATURE"));
        taskTypes.add(new TaskType("IMPROVEMENT"));
        return taskTypes;
    }

    private List<Task> prepareTasks(final User user, final List<Issue> issues, final List<TaskType> taskTypes) {
        final List<Task> tasks = new ArrayList<>();
        int counter = 1;
        for (final Issue issue : issues) {
            for (final TaskType taskType : taskTypes) {
                tasks.add(Task.builder()
                        .name("Test Task " + counter)
                        .description("Description for Test Task " + counter)
                        .issue(issue)
                        .assignee(user)
                        .taskType(taskType)
                        .status(OPEN)
                        .createdAt(now().truncatedTo(MINUTES))
                        .updatedAt(now().truncatedTo(MINUTES))
                        .priority(new Random().nextInt(1, 6))
                        .build());
                counter++;
            }
        }
        return tasks;
    }
}
