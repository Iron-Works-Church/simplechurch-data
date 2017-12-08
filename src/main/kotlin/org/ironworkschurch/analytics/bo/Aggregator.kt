package org.ironworkschurch.analytics.bo

import org.ironworkschurch.analytics.to.FlatGivingTransaction
import org.ironworkschurch.analytics.to.GivingAggregate
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_DATE

class Aggregator {
  fun aggregate(transactions: List<FlatGivingTransaction>): List<GivingAggregate> {
    val transactionDates = transactions.map { it.date }
    val minDate = transactionDates.min()!!
    val maxDate = transactionDates.max()!!

    return transactions.groupBy { it.uid }
      .values
      .flatMap { transactionsForUser ->
        val uid = transactionsForUser[0].uid
        (minDate..maxDate).asSequence()
          .map { date ->
          GivingAggregate(
            uid = uid,
            date = date.format(ISO_DATE),
            previous7DayTotal = windowTotal(date, 7, transactionsForUser),
            previous30DayTotal = windowTotal(date, 30, transactionsForUser),
            previous90DayTotal = windowTotal(date, 90, transactionsForUser),
            previous180DayTotal = windowTotal(date, 180, transactionsForUser),
            previous365DayTotal = windowTotal(date, 365, transactionsForUser)
          )
        }
          .toList()
    }
  }

  private fun ClosedRange<LocalDate>.asSequence() = LocalDateIterator(this).asSequence()

  class LocalDateIterator(private val dateRange: ClosedRange<LocalDate>) : Iterator<LocalDate> {
    private var current = dateRange.start
    override fun hasNext() = current in dateRange
    override fun next(): LocalDate {
      current = current.plusDays(7)
      return current
    }
  }

  private fun windowTotal(endDate: LocalDate, daysToSubtract: Long, transactionsForUser: List<FlatGivingTransaction>): Double {
    val startDate = endDate.minusDays(daysToSubtract)
    val dateRange = startDate..endDate
    return transactionsForUser
      .filter { it.date in dateRange }
      .sumByDouble { it.amount }
  }
}