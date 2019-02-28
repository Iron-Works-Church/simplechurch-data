curl -XPUT localhost:9200/iwc-transaction-rollup '{
  "settings" : {
    "number_of_shards" : 1,
    "number_of_replicas" : 0
  },
  "mappings" : {
    "rollup" : {
      "properties" : {
        "uid" : { "type" : "integer" },
        "date" : { "type" : "date", "format" : "yyyy-MM-dd" },
        "amountPast7" : { "type" : "double" },
        "amountPast30" : { "type" : "double" },
        "amountPast90" : { "type" : "double" },
        "amountPast180" : { "type" : "double" },
        "amountPast365" : { "type" : "double" },
        "isMember" : { "type": "boolean" }
      }
    }
  }
}'