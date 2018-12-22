package org.ironworkschurch.analytics.to

import java.io.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter


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

fun GivingTransaction.flatten() = FlatGivingTransaction(
  id = id,
  uid = uid,
  amount = amount.replace("$", "").replace(",", "").toDouble(),
  date = LocalDate.parse(date, DateTimeFormatter.ISO_DATE),
  time = time,
  method = method,
  transactionId = transactionId,
  subscriptionId = subscriptionId,
  fee = fee,
  note = note,
  checkId = checkId,
  batchId = batchId,
  pledgeId = pledgeId,
  checkNumber = checkNumber,
  oldNote = oldNote,
  sfoSynced = sfoSynced,
  qboSynced = qboSynced,
  categoryId = category.id
)


data class GivingAggregate  (
  val uid: Int,
  val date: String,
  val previous7DayTotal: Double,
  val previous30DayTotal: Double,
  val previous90DayTotal: Double,
  val previous180DayTotal: Double,
  val previous365DayTotal: Double
) : Serializable