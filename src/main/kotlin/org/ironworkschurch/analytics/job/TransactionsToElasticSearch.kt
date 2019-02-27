package org.ironworkschurch.analytics.job

import com.google.inject.Guice
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.ironworkschurch.analytics.bo.ElasticManager
import org.ironworkschurch.analytics.config.ApiModule
import org.ironworkschurch.analytics.config.AppModule
import org.ironworkschurch.analytics.config.ElasticSearchModule
import org.ironworkschurch.analytics.config.EtlModule
import org.ironworkschurch.analytics.dao.TransactionDao
import sun.rmi.transport.Transport
import java.net.InetAddress
import javax.inject.Inject

class TransactionsToElasticSearch @Inject constructor(
  private val transactionDao: TransactionDao,
  private val elasticManager: ElasticManager
) {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Guice.createInjector(EtlModule(), ApiModule(), AppModule(), ElasticSearchModule())
        .getInstance(TransactionsToElasticSearch::class.java).run()
    }
  }

  fun run() {
    elasticManager.loadRollupsToElastic()
    /*val map = hashMapOf<String, String>()
    val settings = Settings.builder()
      .put("client.transport.ignore_cluster_name", true)
      .build()
    val transportAddress = TransportAddress(InetAddress.getByName("localhost"), 9200)

    val transportClient = PreBuiltTransportClient(settings)
      .addTransportAddress(transportAddress)
    val indices = transportClient.admin().indices()
    println(indices.)*/
  }
}