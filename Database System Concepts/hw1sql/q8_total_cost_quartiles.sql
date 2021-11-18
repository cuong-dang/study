SELECT
    CompanyName,
    CustomerId,
    TotalExpenditures
FROM (
    SELECT
        ifnull(CompanyName, 'MISSING_NAME') AS CompanyName,
        CustomerId,
        sum(UnitPrice * Quantity) AS TotalExpenditures,
        NTILE(4) OVER (
            ORDER BY sum(UnitPrice * Quantity)
        ) AS Quartile
    FROM
        'Order' o LEFT JOIN
        OrderDetail od ON o.Id = od.OrderId LEFT JOIN
        Customer c ON o.CustomerId = c.Id
    GROUP BY CompanyName, CustomerId
)
WHERE Quartile = 1;
