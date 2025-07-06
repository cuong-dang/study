WITH t3 AS (
    SELECT player_id, event_date, RANK() OVER (PARTITION BY player_id ORDER BY event_date) AS date_rank
    FROM Activity
),
t4 AS (
    SELECT *
    FROM t3
    WHERE date_rank <= 2
),
t1 AS (
    SELECT a1.player_id
    FROM t4 a1 JOIN t4 a2 ON a1.player_id = a2.player_id AND a1.event_date = a2.event_date - interval '1' day
),
t2 AS (
    SELECT DISTINCT player_id
    FROM Activity
)
SELECT round(count(CASE WHEN t1.player_id IS NOT NULL THEN 1 ELSE NULL END)::decimal / count(*), 2) AS fraction
FROM t2 LEFT JOIN t1 ON t2.player_id = t1.player_id;
