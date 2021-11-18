SELECT
    ProductName,
    CompanyName,
    ContactName
FROM
    (
        SELECT
            p.ProductName,
            min(o.Id) AS Id
        FROM
            Product p JOIN
            OrderDetail od ON p.Id = od.ProductId JOIN
            'Order' o ON od.OrderId = o.Id
        WHERE
            p.Discontinued = 1
        GROUP BY p.ProductName
    ) fo JOIN
    'Order' o ON fo.Id = o.Id
    JOIN Customer c ON o.CustomerId = c.Id
ORDER BY
    ProductName;
