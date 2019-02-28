# SimpleChurch data analysis

## Setup

* Run `docker-compose -f config/docker-compose.yml up -d` to stand up MySQL, ElasticSearch, and Kibana
* Populate `config.properties`, `simplechurch-login.properties`, and `spark.properties` (see the respective `.sample` files)
  with credentials and connection details

## Financial analysis

Run `UpdatePeople`, `UpdateTransactions`, then `TransactionsToElasticSearch`.
This populates the `iwc-transaction-rollup` index in ElasticSearch.