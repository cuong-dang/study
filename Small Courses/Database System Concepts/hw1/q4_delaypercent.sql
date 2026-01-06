SELECT
    CompanyName,
    ROUND(CAST(COUNT(
        CASE
            WHEN DATETIME(ShippedDate) > DATETIME(RequiredDate) THEN 1
            ELSE NULL
        END
    ) AS REAL) / COUNT(*) * 100, 2) AS PercLate
FROM 'Order' JOIN Shipper ON 'Order'.ShipVia = Shipper.Id
GROUP BY CompanyName
ORDER BY PercLate DESC;
