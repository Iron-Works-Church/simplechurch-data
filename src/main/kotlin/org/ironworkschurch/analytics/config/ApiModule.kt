package org.ironworkschurch.analytics.config

import com.google.gson.Gson
import com.google.inject.AbstractModule
import com.google.inject.Provides
import org.ironworkschurch.analytics.dao.SimpleChurchDao
import org.ironworkschurch.analytics.dao.SimpleChurchDaoImpl
import javax.inject.Singleton

class ApiModule : AbstractModule() {
  override fun configure() {
  }

  @Provides
  @Singleton
  fun getSimpleChurchDao(gson: Gson) : SimpleChurchDao {
    return SimpleChurchDaoImpl(gson)
  }
}