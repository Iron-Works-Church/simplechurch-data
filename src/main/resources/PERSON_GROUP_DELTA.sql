create table if not exists PERSON_GROUP_DELTA
(
  did int AUTO_INCREMENT PRIMARY KEY,
	uid int not null,
	gid int not null,
	name text null,
	ROW_EFF_DTS datetime default CURRENT_TIMESTAMP not null,
	ROW_EXP_DTS datetime null,
	CURR_ROW_FL varchar(1) default 'Y' not null,
	LAST_UPDATE_DTS datetime default CURRENT_TIMESTAMP not null,
	UNIQUE KEY bus_key (uid, gid)
)
;

