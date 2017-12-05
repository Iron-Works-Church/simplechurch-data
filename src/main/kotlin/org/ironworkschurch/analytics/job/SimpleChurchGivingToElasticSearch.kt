package org.ironworkschurch.analytics.job

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark
import org.ironworkschurch.analytics.bo.SimpleChurchManager
import org.ironworkschurch.analytics.to.FlatGivingTransaction
import org.ironworkschurch.analytics.to.GivingTransaction
import java.io.File


class SimpleChurchGivingToElasticSearch (val simpleChurchManager: SimpleChurchManager) {
  fun simpleChurchGivingToElasticSearch() {
    val givingByHousehold = simpleChurchManager.getGivingByHousehold()
    //val givingByHousehold: List<GivingTransaction> = Gson().fromJson(File("transactions.json").reader(), object: TypeToken<List<GivingTransaction>>() {}.type)


    val sparkConf = SparkConf().apply {
      set("es.index.auto.create", "true")
      set("es.nodes", "10.8.0.10")
      set("es.port", "9200")
      set("es.nodes.wan.only", "true")
    }

    val sc = JavaSparkContext("local", "iwc", sparkConf)
    val flatTransactions = givingByHousehold.map {
      FlatGivingTransaction(
        id = it.id,
        uid = it.uid,
        amount = it.amount.replace("$", "").replace(",", "").toDouble(),
        date = it.date,
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
        categoryId = it.category.id,
        categoryName = it.category.name
      )
    }

    //JavaEsSpark.saveToEs(sc.parallelize(flatTransactions), "iwc-giving/transactions")
    val aggregates = org.ironworkschurch.analytics.bo.Aggregator().aggregate(flatTransactions)

    JavaEsSpark.saveToEs(sc.parallelize(aggregates), "iwc-giving-aggregates/aggregates")

  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      SimpleChurchGivingToElasticSearch(SimpleChurchManager()).simpleChurchGivingToElasticSearch()
    }
  }
}