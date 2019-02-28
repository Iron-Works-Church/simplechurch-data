INSERT INTO TRANSACTION_ROLLUP
SELECT
  D.uid,
  D.date,
  COALESCE(T7.amount, 0) AS amount_last_7,
  COALESCE(T30.amount, 0) AS amount_last_30,
  COALESCE(T90.amount, 0) AS amount_last_90,
  COALESCE(T180.amount, 0) AS amount_last_180,
  COALESCE(T365.amount, 0) AS amount_last_365,
  D.IS_MEMBER
FROM (SELECT *
      FROM
        (
          SELECT DISTINCT
            COALESCE(F.primaryUid, P.uid) uid,
            CASE PGB.gid WHEN 18 THEN true ELSE false END as IS_MEMBER
          FROM
            PERSON_BASE P
            LEFT OUTER JOIN FAMILY_MEMBER_BASE F ON P.uid = F.uid
            LEFT OUTER JOIN PERSON_GROUP_BASE PGB
              ON PGB.uid = COALESCE(F.primaryUid, P.uid) AND PGB.gid = 18
        ) GIVING_UNITS
        , SUNDAYS S) D
  LEFT JOIN
  (SELECT
     T.uid,
     S.date,
     SUM(amount) AS amount
   FROM TRANSACTION_BASE T
     INNER JOIN SUNDAYS S ON T.date BETWEEN DATE_ADD(S.date, INTERVAL -7 DAY) AND S.date
   GROUP BY T.uid, S.date
  ) T7 ON T7.uid = D.uid AND T7.date = D.date
  LEFT JOIN
  (SELECT
     T.uid,
     S.date,
     SUM(amount) AS amount
   FROM TRANSACTION_BASE T
     INNER JOIN SUNDAYS S ON T.date BETWEEN DATE_ADD(S.date, INTERVAL -30 DAY) AND S.date
   GROUP BY T.uid, S.date
  ) T30 ON T30.uid = D.uid AND T30.date = D.date
  LEFT JOIN
  (SELECT
     T.uid,
     S.date,
     SUM(amount) AS amount
   FROM TRANSACTION_BASE T
     INNER JOIN SUNDAYS S ON T.date BETWEEN DATE_ADD(S.date, INTERVAL -90 DAY) AND S.date
   GROUP BY T.uid, S.date
  ) T90 ON T90.uid = D.uid AND T90.date = D.date
  LEFT JOIN
  (SELECT
     T.uid,
     S.date,
     SUM(amount) AS amount
   FROM TRANSACTION_BASE T
     INNER JOIN SUNDAYS S ON T.date BETWEEN DATE_ADD(S.date, INTERVAL -180 DAY) AND S.date
   GROUP BY T.uid, S.date
  ) T180 ON T180.uid = D.uid AND T180.date = D.date
  LEFT JOIN
  (SELECT
     T.uid,
     S.date,
     SUM(amount) AS amount
   FROM TRANSACTION_BASE T
     INNER JOIN SUNDAYS S ON T.date BETWEEN DATE_ADD(S.date, INTERVAL -365 DAY) AND S.date
   GROUP BY T.uid, S.date
  ) T365 ON T365.uid = D.uid AND T365.date = D.date
WHERE D.date BETWEEN (SELECT COALESCE(DATE_ADD(MAX(date), INTERVAL 1 DAY), '1900-01-01') FROM TRANSACTION_ROLLUP) AND CURRENT_TIMESTAMP