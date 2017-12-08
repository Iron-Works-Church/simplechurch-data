package org.ironworkschurch.analytics.to

import java.io.Serializable
import java.time.LocalDate


data class FlatGivingTransaction  (
  val id: Int,
  val uid: Int,
  val amount: Double,
  val date: LocalDate,
  val time: String,
  val method: String,
  val transactionId: String,
  val subscriptionId: String,
  val fee: String,
  val note: String,
  val checkId: String,
  val batchId: String,
  val pledgeId: String,
  val checkNumber: String,
  val oldNote: String,
  val sfoSynced: String,
  val qboSynced: String,
  val categoryId: Int
) : Serializable


data class GivingAggregate  (
  val uid: Int,
  val date: String,
  val previous7DayTotal: Double,
  val previous30DayTotal: Double,
  val previous90DayTotal: Double,
  val previous180DayTotal: Double,
  val previous365DayTotal: Double
) : Serializable