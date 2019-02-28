package org.ironworkschurch.analytics.job

import com.google.common.io.Resources
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.inject.Guice
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark
import org.ironworkschurch.analytics.bo.SimpleChurchManager
import org.ironworkschurch.analytics.config.ApiModule
import org.ironworkschurch.analytics.config.AppModule
import org.ironworkschurch.analytics.config.ElasticSearchModule
import org.ironworkschurch.analytics.config.EtlModule
import org.ironworkschurch.analytics.to.FlatGivingTransaction
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject


class SimpleChurchGivingToElasticSearch @Inject constructor (private val simpleChurchManager: SimpleChurchManager) {
  fun simpleChurchGivingToElasticSearch() {
    val givingByHousehold = simpleChurchManager.getGivingByHousehold()
    //val givingByHousehold: List<GivingTransaction> = Gson().fromJson(File("transactions.json").reader(), object: TypeToken<List<GivingTransaction>>() {}.type)

    val properties = File("config/spark.properties").reader().use {
      Properties().apply { load(it) }
    }

    val sparkConf = SparkConf().apply {
      set("es.index.auto.create", properties.getProperty("es.index.auto.create"))
      set("es.nodes", properties.getProperty("es.nodes"))
      set("es.port", properties.getProperty("es.port"))
      set("es.nodes.wan.only", properties.getProperty("es.nodes.wan.only"))
    }

    val sc = JavaSparkContext("local", "iwc", sparkConf)
    val flatTransactions = givingByHousehold.map {
      FlatGivingTransaction(
        id = it.id,
        uid = it.uid,
        amount = it.amount.replace("$", "").replace(",", "").toDouble(),
        date = LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE),
        time = it.time,
        method = it.method,
        transactionId = it.transactionId,
        subscriptionId = it.subscriptionId,
        fee = it.fee,
        note = it.note,
        checkId = it.checkId,
        batchId = it.batchId,
        pledgeId = it.pledgeId,
        checkNumber = it.checkNumber,
        oldNote = it.oldNote,
        sfoSynced = it.sfoSynced,
        qboSynced = it.qboSynced,
        categoryId = it.category.id
      )
    }

    JavaEsSpark.saveToEs(sc.parallelize(flatTransactions), "iwc-giving/transactions")
    val aggregates = org.ironworkschurch.analytics.bo.Aggregator().aggregate(flatTransactions)

    JavaEsSpark.saveToEs(sc.parallelize(aggregates), "iwc-giving-aggregates/aggregates")
  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Guice.createInjector(EtlModule(), ApiModule(), AppModule(), ElasticSearchModule())
        .getInstance(SimpleChurchGivingToElasticSearch::class.java)
        .simpleChurchGivingToElasticSearch()
    }
  }
}