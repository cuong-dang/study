SELECT
    ProductName,
    CompanyName,
    ContactName
FROM
    (
        SELECT
            p.ProductName,
            min(o.OrderDate) AS OrderDate
        FROM
            Product p JOIN
            OrderDetail od ON p.Id = od.ProductId JOIN
            'Order' o ON od.OrderId = o.Id
        WHERE
            p.Discontinued = 1
        GROUP BY p.ProductName
    ) fo JOIN
    'Order' o ON fo.OrderDate = o.OrderDate
    JOIN Customer c ON o.CustomerId = c.Id
ORDER BY
    ProductName;
