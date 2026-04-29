USE bo1_sales_db;

CREATE TABLE IF NOT EXISTS product_sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_date VARCHAR(10),
    region VARCHAR(50),
    product VARCHAR(50),
    qty INT,
    cost DECIMAL(10,2),
    amt DECIMAL(10,2),
    tax DECIMAL(10,2),
    total DECIMAL(10,2),
    sync INT DEFAULT 0
);

-- BO1 gets the EAST region data
INSERT IGNORE INTO product_sales (id, sale_date, region, product, qty, cost, amt, tax, total)
VALUES
(1, '1-Apr', 'East', 'Paper', 73, 12.95, 945.35, 66.17, 1011.52),
(2, '2-Apr', 'East', 'Pens', 14, 2.19, 30.66, 2.15, 32.81),
(3, '3-Apr', 'East', 'Paper', 21, 12.95, 271.95, 19.04, 290.99);