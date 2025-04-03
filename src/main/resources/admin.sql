CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (username, email, password, role)
VALUES (
           'tab_admin',
           'tab@gmail.com',
           crypt('admin', gen_salt('bf')),
           'ADMIN'
       );