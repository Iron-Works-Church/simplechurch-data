package org.ironworkschurch.analytics.bo

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.spark.network.client.TransportClientFactory
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.common.xcontent.XContentType.JSON
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.max.Max
import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.ironworkschurch.analytics.dao.TransactionDao
import java.net.InetAddress
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject
import org.bouncycastle.crypto.tls.ConnectionEnd.client




class ElasticManager @Inject constructor (private val transactionDao: TransactionDao,
                                          private val transportAddress: TransportAddress) {
  private val objectMapper = ObjectMapper()

  fun loadToElasticSearch(list: List<Any>, index: String, type: String) {
    if (list.isEmpty()) {
      println("No documents to load")
      return
    }

    val client = getClient()

    val bulk = client.prepareBulk()
    list.map { objectMapper.writeValueAsBytes(it) }
      .map { client.prepareIndex(index, type).setSource(it, JSON) }
      .forEach { bulk.add(it) }

    val bulkResponse = bulk.get()
    if (bulkResponse.hasFailures()) {
      println(bulkResponse.buildFailureMessage())
    }
  }

  private fun getClient(): TransportClient {

    val settings = Settings.builder()
      .put("client.transport.ignore_cluster_name", true)
      //.put("cluster.name", "docker-cluster")
      .build()
    return PreBuiltTransportClient(settings).addTransportAddress(transportAddress)
  }

  fun getLastRollupDate(): LocalDate? {
    val client = getClient()
    val aggregation = AggregationBuilders.max("max").field("date")
    val sr = client
      .prepareSearch("iwc-transaction-rollup")
      .setQuery(QueryBuilders.matchAllQuery())
      .addAggregation(aggregation)
      .get()

    val agg1 = sr.aggregations.get<Max>("max")

    return when  {
      agg1 == null -> null
      agg1.value == Double.NEGATIVE_INFINITY -> null
      else -> try {
        LocalDate.parse(agg1.valueAsString, DateTimeFormatter.ISO_DATE)
      } catch (e: DateTimeParseException) {
        null
      }
    }
  }

  fun loadRollupsToElastic() {
    val lastRollupDate = getLastRollupDate()
    loadToElasticSearch(transactionDao.getRollups(lastRollupDate), "iwc-transaction-rollup", "rollup")
  }
}
