ALTER TABLE users
    ADD COLUMN password VARCHAR(255) NOT NULL DEFAULT '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqJfJCzJ8XyLz0x/Jh0JLZxJHgD9q',
    ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'USER';

UPDATE users SET role = 'ADMIN' WHERE username = 'john_doe';
UPDATE users SET role = 'USER' WHERE username = 'jane_smith';
