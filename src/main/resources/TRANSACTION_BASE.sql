create table if not exists TRANSACTION_BASE
(
	id int NOT NULL,
	uid int NOT NULL,
	amount double NOT NULL,
	date date NOT NULL,
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
  UNIQUE KEY bus_key (id)
)
;

