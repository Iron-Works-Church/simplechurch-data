create table if not exists PERSON_GROUP_BASE
(
  uid int not null,
	gid int not null,
	name text null,
	UNIQUE KEY bus_key (uid, gid)
)
;

