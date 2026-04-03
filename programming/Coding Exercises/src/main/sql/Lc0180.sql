WITH t AS (
    SELECT num
         , lag(num) OVER (ORDER BY id) AS lag
         , lead(num) OVER (ORDER BY id) AS lead
    FROM Logs
)
SELECT DISTINCT num AS ConsecutiveNums
FROM t
WHERE t.num = t.lag AND t.num = t.lead;
