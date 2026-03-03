WITH t1 AS (
    SELECT user_id,
           count(CASE WHEN action = 'confirmed' THEN 1 ELSE NULL END) AS num_confirmed,
           count(*) AS num_actions
    FROM Confirmations
    GROUP BY user_id
)
SELECT s.user_id,
       CASE WHEN num_actions IS NULL THEN 0.00
            ELSE round(num_confirmed::decimal / num_actions, 2)
       END AS confirmation_rate
FROM Signups s LEFT JOIN t1 ON s.user_id = t1.user_id;
