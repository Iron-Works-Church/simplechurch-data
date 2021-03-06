package org.ironworkschurch.analytics.config

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import org.elasticsearch.common.transport.TransportAddress
import java.io.File
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.*

class ElasticSearchModule : AbstractModule() {
  override fun configure() {
  }

  @Provides
  @Singleton
  fun elasticSearchAddress(): TransportAddress {
    val properties = File("config/elasticsearch.properties").reader().use {
      Properties().apply { load(it) }
    }

    val hostName = properties.getProperty("es.hostname")
    val port = properties.getProperty("es.port").toInt()

    return TransportAddress(InetAddress.getByName(hostName),port)
  }
}
