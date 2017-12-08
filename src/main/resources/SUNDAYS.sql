CREATE TABLE IF NOT EXISTS SUNDAYS (
  date date not null primary key
);

INSERT INTO SUNDAYS VALUES ('2015-01-04');

INSERT INTO SUNDAYS (
  WITH RECURSIVE cte (date) AS (SELECT MAX(date)
  FROM   SUNDAYS
  UNION ALL
  SELECT Date_add(date, INTERVAL 7 DAY)
  FROM   cte
  WHERE  date < '2030-01-01')

  SELECT date
  FROM   cte C
  WHERE C.date > (SELECT MAX(date) FROM SUNDAYS)
);