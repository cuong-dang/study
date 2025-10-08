WITH t1 AS (
    SELECT product_id, max(change_date) AS last_date
    FROM Products
    WHERE change_date <= '2019-08-16'
    GROUP BY product_id
),
t2 AS (
    SELECT DISTINCT product_id, 10 AS default_price
    FROM Products
)
SELECT t2.product_id, coalesce(p.new_price, t2.default_price) AS price
FROM t2 LEFT JOIN
     (Products p JOIN t1 ON p.product_id = t1.product_id AND
                            p.change_date = t1.last_date)
     ON t2.product_id = p.product_id;
