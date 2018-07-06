create table if not exists PERSON_BASE
(
	uid int not null,
	address text null,
	address2 text null,
	address2EndDate text null,
	address2Label text null,
	address2StartDate text null,
	addressEndDate text null,
	addressLabel text null,
	addressStartDate text null,
	canEdit bit not null,
	canEditAllFields bit not null,
	canEditPicture bit not null,
	city text null,
	city2 text null,
	country text null,
	country2 text null,
	date1 text null,
	dateBaptism text null,
	dateBirth text null,
	dateDied text null,
	envNum int not null,
	fname text null,
	hasPicture bit not null,
	hasUnApprovedChanges bit not null,
	lname text null,
	mail text null,
	male int not null,
	name text null,
	phoneCell text null,
	phoneHome text null,
	phoneWork text null,
	preferredName text null,
	rfidtag text null,
	rfidtagAlt text null,
	secondaryEmail text null,
	state text null,
	state2 text null,
	updated text null,
	zipcode text null,
	zipcode2 text null,
	UNIQUE KEY bus_key (uid)
)
;
