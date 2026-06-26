CREATE TABLE IF NOT EXISTS inventory_transaction (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    type VARCHAR(50) NOT NULL,
    reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_product FOREIGN KEY (product_id) REFERENCES product(id)
);
