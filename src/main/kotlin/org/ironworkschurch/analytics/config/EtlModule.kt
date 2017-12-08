package org.ironworkschurch.analytics.config

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.mysql.cj.jdbc.MysqlDataSource
import com.zaxxer.hikari.HikariConfig
import org.springframework.jdbc.core.JdbcTemplate
import java.io.File
import java.util.*
import javax.inject.Singleton

class EtlModule : AbstractModule() {
  override fun configure() {

  }

  @Provides
  @Singleton
  fun getMySqlProperties(): Properties {
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

  @Provides
  @Singleton
  fun getMySqlUrl(configProperties: Properties): String {
    return "jdbc:mysql://${configProperties.getProperty("mysql.host")}:${configProperties.getProperty("mysql.port")}/${configProperties.getProperty("mysql.database")}?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false"
  }

  @Provides
  @Singleton
  fun getJdbcTemplate(mySqlUrl: String, configProperties: Properties): JdbcTemplate {
    val dataSource = MysqlDataSource().apply {
      setURL(mySqlUrl)
      databaseName = "iwc_members"
      user = configProperties.getProperty("mysql.user")
      setPassword(configProperties.getProperty("mysql.password"))
    }
    val hcpdataSource = HikariConfig().apply {
      setDataSource(dataSource)
      jdbcUrl = mySqlUrl
      username = configProperties.getProperty("mysql.user")
      password = configProperties.getProperty("mysql.password")
    }.dataSource

    return JdbcTemplate(hcpdataSource)
  }
}