package org.ironworkschurch.analytics.job

import com.google.common.base.Stopwatch
import com.google.inject.Guice
import mu.KotlinLogging
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.SaveMode
import org.ironworkschurch.analytics.bo.DeltaManager
import org.ironworkschurch.analytics.bo.SimpleChurchManager
import org.ironworkschurch.analytics.config.ApiModule
import org.ironworkschurch.analytics.config.EtlModule
import java.io.File
import java.util.*
import javax.inject.Inject

class SimpleChurchToMySql @Inject constructor (private val simpleChurchManager: SimpleChurchManager,
                                               private val deltaManager: DeltaManager,
                                               private val configProperties: Properties,
                                               private val mySqlUrl: String) {
  companion object {
    val logger = KotlinLogging.logger {}

    @JvmStatic
    fun main(args: Array<String>) {
      Guice.createInjector(EtlModule(), ApiModule()).getInstance(SimpleChurchToMySql::class.java).extractAndLoad()
    }
  }

  private fun extractAndLoad() {
    logger.info { "Beginning extract and load from SimpleChurch API to MySQL database" }

    logger.debug { "Configuration retrieved successfully" }

    logger.debug { "Retrieving user data" }
    val stopwatch = Stopwatch.createStarted()
    val personHeaders = simpleChurchManager.getPersonHeaders()
    val personDetails = simpleChurchManager.getPersonDetails(personHeaders)
    stopwatch.stop()
    logger.debug { "Fetched data in $stopwatch" }

    stopwatch.reset().start()
    val sc = JavaSparkContext("local", "iwc", SparkConf())
    val sqlContext = SQLContext.getOrCreate(sc.sc())
    stopwatch.stop()
    logger.debug { "Constructed context in $stopwatch" }
    val mySqlContext = MySqlContext(
      sqlContext = sqlContext,
      mySqlUrl = mySqlUrl,
      sqlCredentials = Properties().apply {
        setProperty("user", configProperties.getProperty("mysql.user"))
        setProperty("password", configProperties.getProperty("mysql.password"))
      }
    )

    logger.debug { "Writing data to MySQL database" }
    stopwatch.reset().start()
    mySqlContext.writeToTable(personHeaders, "PERSON_HEADER_BASE")
    mySqlContext.writeToTable(simpleChurchManager.getFlatPersonDetails(personDetails), "PERSON_DETAIL_BASE")
    mySqlContext.writeToTable(simpleChurchManager.getFamilyMembers(personDetails), "FAMILY_MEMBER_BASE")
    mySqlContext.writeToTable(simpleChurchManager.getPersonGroups(personDetails), "PERSON_GROUP_BASE")
    mySqlContext.writeToTable(simpleChurchManager.getGroupHeaders(personDetails), "GROUP_BASE")
    stopwatch.stop()
    logger.debug { "Wrote data in $stopwatch" }

    logger.debug { "Copying data to history" }
    stopwatch.reset().start()
    deltaManager.loadDeltaTables()
    stopwatch.stop()
    logger.debug { "Completed copying to history in $stopwatch" }
    logger.info { "Completed extract and load process" }
  }


  data class MySqlContext(val sqlContext: SQLContext, val mySqlUrl: String, val sqlCredentials: Properties) {
    inline fun <reified T : Any> writeToTable(data: List<T>, table: String) {
      logger.debug { "Writing data to $table" }
      sqlContext.createDataFrame(data, T::class.java)
        .write()
        .mode(SaveMode.Overwrite)
        .jdbc(mySqlUrl, table, sqlCredentials)
    }
  }
}