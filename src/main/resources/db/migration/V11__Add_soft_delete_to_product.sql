ALTER TABLE product ADD COLUMN deleted_at TIMESTAMP DEFAULT NULL;

CREATE INDEX idx_product_deleted_at ON product(deleted_at);
CREATE INDEX idx_product_name ON product(name);
CREATE INDEX idx_review_product_id ON reviews(product_id);
CREATE INDEX idx_order_item_order_id ON order_items(order_id);
