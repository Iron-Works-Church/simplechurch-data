package org.ironworkschurch.analytics.config

import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors.getExitingExecutorService
import com.google.common.util.concurrent.MoreExecutors.listeningDecorator
import com.google.inject.AbstractModule
import com.google.inject.Provides
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import javax.inject.Singleton

class AppModule : AbstractModule() {
  override fun configure() {
  }

  @Provides
  @Singleton
  fun getExecutorService(): ListeningExecutorService {
    val tpe = Executors.newFixedThreadPool(8)
    return listeningDecorator(getExitingExecutorService(tpe as ThreadPoolExecutor))
  }
}