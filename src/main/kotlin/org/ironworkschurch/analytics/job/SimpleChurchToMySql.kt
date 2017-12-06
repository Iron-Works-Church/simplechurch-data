package org.ironworkschurch.analytics.job

import com.google.common.base.Stopwatch
import mu.KotlinLogging
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.SaveMode
import org.ironworkschurch.analytics.bo.SimpleChurchManager
import java.io.File
import java.util.*

class SimpleChurchToMySql(private val simpleChurchManager: SimpleChurchManager) {
  companion object {
    val logger = KotlinLogging.logger {}

    @JvmStatic
    fun main(args: Array<String>) {
      SimpleChurchToMySql(SimpleChurchManager()).extractAndLoad()
    }
  }

  private fun extractAndLoad() {
    logger.info { "Beginning extract and load from SimpleChurch API to MySQL database" }
    val configProperties: Properties = getConfigProperties()

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
      mySqlUrl = "jdbc:mysql://${configProperties.getProperty("mysql.host")}:${configProperties.getProperty("mysql.port")}/${configProperties.getProperty("mysql.database")}?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false",
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
    logger.info { "Completed extract and load process" }
  }

  private fun getConfigProperties(): Properties {
    val configFile = File("config/config.properties")
    if (!configFile.exists()) {
      throw RuntimeException("Required file ${configFile.absolutePath} is missing")
    }

    val configProperties: Properties = configFile.inputStream().use {
      Properties().apply {
        load(it)
      }
    }

    val missingProperties = listOf(
      "mysql.host",
      "mysql.port",
      "mysql.database",
      "mysql.user",
      "mysql.password"
    ).filter { configProperties.getProperty(it).isNullOrEmpty() }

    if (missingProperties.isNotEmpty()) {
      throw RuntimeException("The following properties were not configured in ${configFile.path}: ${missingProperties.joinToString()}")
    }
    return configProperties
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