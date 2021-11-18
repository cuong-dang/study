SELECT
    DISTINCT
    RegionDescription,
    FirstName,
    LastName,
    BirthDate
FROM
    Employee e JOIN
    EmployeeTerritory et ON e.Id = et.EmployeeId JOIN
    Territory t ON et.TerritoryId = t.Id JOIN
    Region r ON t.RegionId = r.Id
WHERE
    e.BirthDate IN (
        SELECT
            max(BirthDate)
        FROM
            Employee e JOIN
            EmployeeTerritory et ON e.Id = et.EmployeeId JOIN
            Territory t ON et.TerritoryId = t.Id JOIN
            Region r ON t.RegionId = r.Id
        GROUP BY RegionDescription
    );
