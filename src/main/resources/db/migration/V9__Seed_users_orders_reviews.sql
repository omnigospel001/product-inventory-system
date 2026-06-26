-- Seed Users
INSERT INTO users (username, email) VALUES
('john_doe', 'john@example.com'),
('jane_smith', 'jane@example.com');

-- Seed Orders (user_id 1 = john, 2 = jane)
INSERT INTO orders (user_id, status, total_amount) VALUES
(1, 'COMPLETED', 1019.98),
(2, 'PENDING', 34.49);

-- Seed Order Items
-- Order 1 (john): 1 Laptop + 1 T-Shirt
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 1, 1, 999.99),
(1, 2, 1, 19.99);

-- Order 2 (jane): 1 Coffee Beans + 1 T-Shirt
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(2, 3, 1, 14.50),
(2, 2, 1, 19.99);

-- Seed Reviews
INSERT INTO reviews (product_id, user_id, rating, comment) VALUES
(1, 1, 5, 'Excellent laptop, very fast!'),
(2, 2, 4, 'Good quality but runs a bit small.'),
(3, 1, 5, 'Best coffee I have ever tasted.');
