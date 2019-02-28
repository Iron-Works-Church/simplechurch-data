create table if not exists GIVING_CATEGORY_BASE
(
  id int not null,
  name varchar(255) not null,
  sortOrder int not null,
  active bit not null,
  taxDeductible bit not null,
  onlineGivingEnabled bit not null,
  UNIQUE KEY bus_key (id)
)
;

