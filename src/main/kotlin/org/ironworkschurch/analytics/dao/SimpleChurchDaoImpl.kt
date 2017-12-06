package org.ironworkschurch.analytics.dao

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import org.ironworkschurch.analytics.to.Login
import org.ironworkschurch.analytics.to.LoginPayload
import java.io.File
import java.util.*

class SimpleChurchDaoImpl : SimpleChurchDao() {
  private val sessionId: String by lazy {
    login().session_id
  }

  fun login(): Login {
    return objectMapper.readValue(getLoginPayload(), LoginPayload::class.java).data
  }

  fun getLoginPayload(): String {
    val file = File("config/simplechurch-login.properties")
    if (!file.exists()) {
      throw RuntimeException("Missing SimpleChurch credentials file at \"config/simplechurch-login.properties\"")
    }

    val properties = Properties().apply {
      file.bufferedReader().use { load(it) }
    }

    val payload = "https://iwc.simplechurchcrm.com/api/user/login"
      .httpPost(listOf("username" to properties.getProperty("username"), "password" to properties.getProperty("password")))
      .header("User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36")
      .header("Accept" to "application/json")
      .header("Save-Data" to "on")
      .header("Origin" to "https://iwc.simplechurchcrm.com")
      .responseString()
      .third
      .get()
    return payload
  }

  override fun getAllPeoplePayload() = "https://iwc.simplechurchcrm.com/api/groups/66/people".simpleChurchRestGet()

  override fun getPersonDetailsPayload(id: Int) = "https://iwc.simplechurchcrm.com/api/people/$id".simpleChurchRestGet()

  override fun getGivingPayload(id: Int) = "https://iwc.simplechurchcrm.com/api/people/$id/giving".simpleChurchRestGet()

  private fun String.simpleChurchRestGet(): String {
    val (_, response, result) = httpGet()
      .header("X-SessionID" to sessionId)
      .responseString()
    return when (response.httpStatusCode) {
      in 200..299 -> result.get()
      else -> throw RuntimeException(response.httpResponseMessage)
    }
  }
}