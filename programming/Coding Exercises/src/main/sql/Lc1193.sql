SELECT date_format(trans_date, '%Y-%m') AS `month`
     , country
     , count(*) AS trans_count
     , count(if(state = 'approved', 1, null)) AS approved_count
     , sum(amount) AS trans_total_amount
     , sum(if(state = 'approved', amount, 0)) as approved_total_amount
FROM Transactions
GROUP BY month, country;
