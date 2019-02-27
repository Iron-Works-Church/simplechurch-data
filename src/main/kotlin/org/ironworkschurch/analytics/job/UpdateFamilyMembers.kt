package org.ironworkschurch.analytics.job

import com.google.common.base.Stopwatch
import com.google.inject.Guice
import mu.KotlinLogging
import org.ironworkschurch.analytics.bo.BaseManager
import org.ironworkschurch.analytics.bo.DeltaManager
import org.ironworkschurch.analytics.bo.MetadataManager
import org.ironworkschurch.analytics.bo.SimpleChurchManager
import org.ironworkschurch.analytics.config.ApiModule
import org.ironworkschurch.analytics.config.AppModule
import org.ironworkschurch.analytics.config.ElasticSearchModule
import org.ironworkschurch.analytics.config.EtlModule
import org.ironworkschurch.analytics.to.PersonDetails
import javax.inject.Inject

class UpdateFamilyMembers {
}
