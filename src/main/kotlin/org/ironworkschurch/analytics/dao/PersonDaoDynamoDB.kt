package org.ironworkschurch.analytics.dao

import mu.KLogging
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.ScanRequest
import javax.inject.Inject

class PersonDaoDynamoDB @Inject constructor(val dynamoDb: DynamoDbClient) {
  companion object : KLogging()

  fun getAllPersonIDs(): List<Int> {
    val request = ScanRequest.builder()
      .tableName("Person")
      .attributesToGet("uid")
      .build()
    return dynamoDb.scan(request).items()
      .mapNotNull { it["uid"] }
      .map { it.n() }
      .map { it.toInt() }
  }
}