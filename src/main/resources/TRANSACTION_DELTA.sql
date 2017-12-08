create table if not exists TRANSACTION_DELTA
(
	did int AUTO_INCREMENT PRIMARY KEY ,
	id int NOT NULL,
	uid int NOT NULL,
	amount double,
	date date,
	time varchar(255),
	method varchar(255),
	transactionId varchar(255),
	subscriptionId varchar(255),
	fee varchar(255),
	note varchar(255),
	checkId varchar(255),
	batchId varchar(255),
	pledgeId varchar(255),
	checkNumber varchar(255),
	oldNote varchar(255),
	sfoSynced varchar(255),
	qboSynced varchar(255),
	categoryId int,
	ROW_EFF_DTS datetime default CURRENT_TIMESTAMP not null,
	ROW_EXP_DTS datetime null,
	CURR_ROW_FL varchar(1) default 'Y' not null,
	LAST_UPDATE_DTS datetime default CURRENT_TIMESTAMP not null,
	UNIQUE KEY bus_key (id, ROW_EFF_DTS)
)
;

