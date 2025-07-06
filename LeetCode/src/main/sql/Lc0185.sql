WITH t AS (
    SELECT t2.name AS Department
         , t1.name AS Employee
         , t1.salary AS Salary
         , dense_rank() OVER (PARTITION BY departmentId ORDER BY t1.salary DESC) as sal_rank
    FROM Employee t1 JOIN Department t2 ON t1.departmentId = t2.id
)
SELECT Department, Employee, Salary
FROM t
WHERE sal_rank <= 3;
