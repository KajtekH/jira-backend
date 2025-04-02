-- Insert users
INSERT INTO users (username, password, email, role)
VALUES
    ('user1', 'password1', 'user1@example.com',  'ADMIN'),
    ('user2', 'password2', 'user2@example.com', 'PRODUCT_MANAGER'),
    ('user3', 'password3', 'user3@example.com',  'ACCOUNT_MANAGER'),
    ('user4', 'password4', 'user4@example.com',  'USER');

-- Insert products
INSERT INTO products (name, description, version, release_date, owner_id)
VALUES
    ('Product1', 'Description1', '1.0', CURRENT_DATE, 5),
    ('Product2', 'Description2', '1.0', CURRENT_DATE, 6);

-- Insert requests
INSERT INTO requests (name, description, status, product_id, account_manager_id, request_type)
VALUES
    ('Request1', 'Description1', 0, 3, 5,1),
    ('Request2', 'Description2', 0, 3, 6,0),
    ('Request3', 'Description3', 0, 4, 7,2),
    ('Request4', 'Description4', 0, 4, 8,1);

-- Insert issues
INSERT INTO issues (name, description, open_date, status, request_id, product_manager_id)
VALUES
    ('Issue2', 'Description2', CURRENT_DATE, 0, 1, 5),
    ('Issue1', 'Description1', CURRENT_DATE, 0, 1, 6),
    ('Issue3', 'Description3', CURRENT_DATE, 0, 2, 7),
    ('Issue4', 'Description4', CURRENT_DATE, 0, 2, 8),
    ('Issue5', 'Description5', CURRENT_DATE, 0, 3, 7),
    ('Issue6', 'Description6', CURRENT_DATE, 0, 3, 6),
    ('Issue7', 'Description7', CURRENT_DATE, 0, 4, 5),
    ('Issue8', 'Description8', CURRENT_DATE, 0, 4, 8);

INSERT INTO tasks (name, description, task_status, issue_id, assignee_id, created_at, updated_at, task_type)
VALUES
    ('Task1', 'Description1', 0, 1, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task2', 'Description2', 1, 1, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,1),
    ('Task3', 'Description3', 0, 2, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task4', 'Description4', 1, 2, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,3),
    ('Task5', 'Description5', 0, 3, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,2),
    ('Task6', 'Description6', 1, 3, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task7', 'Description7', 0, 4, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task8', 'Description8', 1, 4, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task9', 'Description9', 0, 5, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task10', 'Description10', 1, 5, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task11', 'Description11', 0, 6, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task12', 'Description12', 1, 6, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task13', 'Description13', 0, 7, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task14', 'Description14', 1, 7, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task15', 'Description15', 0, 8, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),
    ('Task16', 'Description16', 1, 8, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0);