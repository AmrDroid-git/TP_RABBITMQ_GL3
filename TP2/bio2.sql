USE bo2_sales_db;

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

-- BO2 gets the WEST region data
INSERT INTO product_sales (sale_date, region, product, qty, cost, amt, tax, total) VALUES
('1-Apr', 'West', 'Paper', 33, 12.95, 427.35, 29.91, 457.26),
('2-Apr', 'West', 'Pens',  40,  2.19,  87.60,  6.13,  93.73),
('3-Apr', 'West', 'Paper', 10, 12.95, 129.50,  9.07, 138.57);