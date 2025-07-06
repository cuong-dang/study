WITH dr_count AS (
    SELECT t1.id, t1.name, count(*) AS dr_count
    FROM Employee t1 JOIN Employee t2 ON t1.id = t2.managerId
    GROUP BY t1.id, t1.name
)
SELECT name
FROM dr_count
WHERE dr_count >= 5;
