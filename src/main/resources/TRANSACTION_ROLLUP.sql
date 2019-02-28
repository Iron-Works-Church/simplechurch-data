create table if not exists TRANSACTION_ROLLUP
(
  uid bigint(11) not null,
  date date not null,
  amount_last_7 double not null,
  amount_last_30 double not null,
  amount_last_90 double not null,
  amount_last_180 double not null,
  amount_last_365 double not null,
  IS_MEMBER int(1) default '0' not null
)
;
