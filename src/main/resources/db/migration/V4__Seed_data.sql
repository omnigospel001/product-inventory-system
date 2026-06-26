-- Seed Categories
INSERT INTO category (name, description) VALUES
('Electronics', 'Electronic devices and gadgets'),
('Clothing', 'Apparel and fashion items'),
('Food & Beverages', 'Edible goods and drinks');

-- Seed Products
INSERT INTO product (name, description, price, quantity, category_id) VALUES
('Laptop', 'High-performance laptop', 999.99, 50, (SELECT id FROM category WHERE name = 'Electronics')),
('T-Shirt', 'Cotton casual t-shirt', 19.99, 200, (SELECT id FROM category WHERE name = 'Clothing')),
('Coffee Beans', 'Premium arabica coffee beans', 14.50, 100, (SELECT id FROM category WHERE name = 'Food & Beverages'));

-- Seed Inventory Transactions
INSERT INTO inventory_transaction (product_id, quantity, type, reason) VALUES
((SELECT id FROM product WHERE name = 'Laptop'), 50, 'STOCK_IN', 'Initial stock'),
((SELECT id FROM product WHERE name = 'T-Shirt'), 200, 'STOCK_IN', 'Initial stock'),
((SELECT id FROM product WHERE name = 'Coffee Beans'), 100, 'STOCK_IN', 'Initial stock');
