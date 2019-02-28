create table if not exists FAMILY_MEMBER_BASE
(
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
	UNIQUE KEY bus_key (fid, uid)
)
;

