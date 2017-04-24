CREATE TABLE IF NOT EXISTS customers (
    id BIGINT NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    n_sales INTEGER NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT NOT NULL,
    description VARCHAR(100) NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS sales (
    id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    FOREIGN KEY(customer_id) REFERENCES customers(id),
    FOREIGN KEY(item_id) REFERENCES items(id),
    PRIMARY KEY(id)
);

CREATE INDEX i_sales_cutomer_id ON sales(customer_id);
