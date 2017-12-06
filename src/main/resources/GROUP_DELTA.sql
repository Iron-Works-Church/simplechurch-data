create table if not exists GROUP_DELTA
(
  did int AUTO_INCREMENT PRIMARY KEY,
	gid int not null,
	active bit not null,
	address text null,
	checkinChildLabelCnt int not null,
	checkinGuardianLabelCnt int not null,
	childCheckin bit not null,
	city text null,
	description text null,
	individual bit not null,
	lat int not null,
	lon int not null,
	maxSize int not null,
	meetingDay text null,
	meetingTime text null,
	name text null,
	selfAdd bit not null,
	selfCheckin bit not null,
	selfRequest bit not null,
	state text null,
	visibleMembership bit not null,
	zipcode text null,
	ROW_EFF_DTS datetime default CURRENT_TIMESTAMP not null,
	ROW_EXP_DTS datetime null,
	CURR_ROW_FL varchar(1) default 'Y' not null,
	LAST_UPDATE_DTS datetime default CURRENT_TIMESTAMP not null,
  UNIQUE KEY bus_key (gid)
)
;

