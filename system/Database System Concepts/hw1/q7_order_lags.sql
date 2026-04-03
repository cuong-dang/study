SELECT
    Id,
    OrderDate,
    lag(OrderDate, 1, OrderDate) OVER (ORDER BY OrderDate),
    round(
        julianday(OrderDate) -
        julianday(lag(OrderDate, 1, OrderDate) OVER (ORDER BY OrderDate)), 2)
FROM 'Order'
WHERE CustomerId = 'BLONP'
ORDER BY OrderDate
LIMIT 10;
