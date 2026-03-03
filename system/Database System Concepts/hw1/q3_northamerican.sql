SELECT
    Id,
    ShipCountry,
    CASE
        WHEN ShipCountry IN ('USA', 'Mexico', 'Canada') THEN 'NorthAmerica'
        ELSE 'OtherPlace'
    END
FROM 'Order'
WHERE Id >= 15445
LIMIT 20;
