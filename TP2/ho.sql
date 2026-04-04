USE ho_sales_db;

CREATE TABLE IF NOT EXISTS product_sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_date VARCHAR(10),
    region VARCHAR(50),
    product VARCHAR(50),
    qty INT,
    cost DECIMAL(10,2),
    amt DECIMAL(10,2),
    tax DECIMAL(10,2),
    total DECIMAL(10,2)
);

-- HO starts empty and will be populated via RabbitMQ sync