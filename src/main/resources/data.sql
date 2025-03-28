-- Insert users
INSERT INTO users (username, password, email, account_non_expired, account_non_locked, credentials_non_expired, enabled)
VALUES
    ('user1', 'password1', 'user1@example.com',  true, true, true, true),
    ('user2', 'password2', 'user2@example.com',  true, true, true, true),
    ('user3', 'password3', 'user3@example.com',  true, true, true, true),
    ('user4', 'password4', 'user4@example.com',  true, true, true, true);

-- Insert roles
    INSERT INTO user_roles (user_id, roles)
    VALUES
        (1, 'ROLE_USER'),
        (2, 'ROLE_ADMIN'),
        (3, 'ROLE_PRODMANAGER'),
        (4, 'ROLE_ACCMANAGER');

-- Insert products
INSERT INTO products (name, description, version, release_date, owner_id)
VALUES
    ('Product1', 'Description1', '1.0', CURRENT_DATE, 1),
    ('Product2', 'Description2', '1.0', CURRENT_DATE, 2);

-- Insert requests
INSERT INTO requests (name, description, status, product_id, account_manager_id, request_type)
VALUES
    ('Request1', 'Description1', 0, 1, 3,1),
    ('Request2', 'Description2', 0, 1, 4,0),
    ('Request3', 'Description3', 0, 2, 3,2),
    ('Request4', 'Description4', 0, 2, 4,1);

-- Insert issues
INSERT INTO issues (name, description, open_date, status, request_id, product_manager_id)
VALUES
    ('Issue2', 'Description2', CURRENT_DATE, 0, 1, 2),
    ('Issue1', 'Description1', CURRENT_DATE, 0, 1, 1),
    ('Issue3', 'Description3', CURRENT_DATE, 0, 2, 1),
    ('Issue4', 'Description4', CURRENT_DATE, 0, 2, 2),
    ('Issue5', 'Description5', CURRENT_DATE, 0, 3, 1),
    ('Issue6', 'Description6', CURRENT_DATE, 0, 3, 2),
    ('Issue7', 'Description7', CURRENT_DATE, 0, 4, 1),
    ('Issue8', 'Description8', CURRENT_DATE, 0, 4, 2);

INSERT INTO tasks (name, description, task_status, issue_id, assignee_id, created_at, updated_at, task_type)
VALUES
    ('Task1', 'Description1', 0, 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task2', 'Description2', 1, 1, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,1),
    ('Task3', 'Description3', 0, 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task4', 'Description4', 1, 2, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,3),
    ('Task5', 'Description5', 0, 3, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,2),
    ('Task6', 'Description6', 1, 3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task7', 'Description7', 0, 4, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task8', 'Description8', 1, 4, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task9', 'Description9', 0, 5, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task10', 'Description10', 1, 5, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task11', 'Description11', 0, 6, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task12', 'Description12', 1, 6, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task13', 'Description13', 0, 7, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task14', 'Description14', 1, 7, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task15', 'Description15', 0, 8, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task16', 'Description16', 1, 8, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0);