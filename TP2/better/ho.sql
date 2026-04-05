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




DELIMITER //
CREATE PROCEDURE SyncProductSale(
    IN p_date VARCHAR(20),
    IN p_region VARCHAR(20),
    IN p_product VARCHAR(50),
    IN p_qty INT,
    IN p_cost DECIMAL(10,2),
    IN p_amt DECIMAL(10,2),
    IN p_tax DECIMAL(10,2),
    IN p_total DECIMAL(10,2)
)
BEGIN
    DECLARE v_count INT;
    
    -- Check if this specific sale already exists
    SELECT COUNT(*) INTO v_count 
    FROM product_sales 
    WHERE sale_date = p_date AND region = p_region AND product = p_product;
    
    IF v_count > 0 THEN
        -- It exists! Update the numbers just in case they changed
        UPDATE product_sales 
        SET qty = p_qty, cost = p_cost, amt = p_amt, tax = p_tax, total = p_total
        WHERE sale_date = p_date AND region = p_region AND product = p_product;
    ELSE
        -- It does not exist! Insert a new row
        INSERT INTO product_sales (sale_date, region, product, qty, cost, amt, tax, total)
        VALUES (p_date, p_region, p_product, p_qty, p_cost, p_amt, p_tax, p_total);
    END IF;
END //
DELIMITER ;