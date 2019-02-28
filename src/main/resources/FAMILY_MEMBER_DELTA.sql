create table if not exists FAMILY_MEMBER_DELTA
(
	did int AUTO_INCREMENT PRIMARY KEY,
	fid int not null,
	uid int not null,
	canView bit not null,
	dateBirth text null,
	dateDied text null,
	fname text null,
	givesWithFamily bit not null,
	hasPicture bit not null,
	lname text null,
	male text null,
	preferredName text null,
	primaryUid int not null,
	relationship text null,
	updated text null,
	ROW_EFF_DTS datetime default CURRENT_TIMESTAMP not null,
	ROW_EXP_DTS datetime null,
	CURR_ROW_FL varchar(1) default 'Y' not null,
	LAST_UPDATE_DTS datetime default CURRENT_TIMESTAMP not null,
	UNIQUE KEY bus_key (fid, uid, ROW_EFF_DTS)
)
;

