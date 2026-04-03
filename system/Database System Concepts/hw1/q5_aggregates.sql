SELECT
    CategoryName,
    count(*),
    round(avg(UnitPrice), 2),
    min(UnitPrice),
    max(UnitPrice),
    sum(UnitsOnOrder)
FROM
    Product p JOIN Category c ON p.CategoryId = c.Id
GROUP BY CategoryName
HAVING count(*) > 10
ORDER BY c.Id;
