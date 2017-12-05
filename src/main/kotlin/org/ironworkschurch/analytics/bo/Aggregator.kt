package org.ironworkschurch.analytics.bo

import org.ironworkschurch.analytics.to.FlatGivingTransaction
import org.ironworkschurch.analytics.to.GivingAggregate
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_DATE

class Aggregator {
  fun aggregate(transactions: List<FlatGivingTransaction>): List<GivingAggregate> {
    val transactionDates = transactions.map { it.date }.map { LocalDate.parse(it, ISO_DATE) }
    val minDate = transactionDates.min()!!
    val maxDate = transactionDates.max()!!

    return transactions.groupBy { it.uid }
      .values
      .flatMap { transactionsForUser ->
        val uid = transactionsForUser[0].uid
        LocalDateIterator(minDate..maxDate).asSequence()
          .map { date ->
          GivingAggregate(
            id = 0,
            uid = uid,
            amount = 0.0,
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

/*
    return transactions.map {
      calculateAverages(it, transactions.groupBy { it.uid }[it.uid]!!)
    }*/
  }

  class LocalDateIterator(val dateRange: ClosedRange<LocalDate>) : Iterator<LocalDate> {
    private var current = dateRange.start
    override fun hasNext() = current in dateRange
    override fun next(): LocalDate {
      current = current.plusDays(7)
      return current
    }
  }

  private fun calculateAverages(transaction: FlatGivingTransaction,
                                transactionsForUser: List<FlatGivingTransaction>): Any {
    val endDate = LocalDate.parse(transaction.date, ISO_DATE)

    return GivingAggregate(id = transaction.id,
      uid = transaction.uid,
      amount = transaction.amount,
      date = transaction.date,
      previous7DayTotal = windowTotal(endDate, 7, transactionsForUser),
      previous30DayTotal = windowTotal(endDate, 30, transactionsForUser),
      previous90DayTotal = windowTotal(endDate, 90, transactionsForUser),
      previous180DayTotal = windowTotal(endDate, 180, transactionsForUser),
      previous365DayTotal = windowTotal(endDate, 365, transactionsForUser)
    )
  }

  private fun windowTotal(endDate: LocalDate, daysToSubtract: Long, transactionsForUser: List<FlatGivingTransaction>): Double {
    val startDate = endDate.minusDays(daysToSubtract)
    return transactionsForUser
      .filter { LocalDate.parse(it.date, ISO_DATE) in startDate..endDate }
      .sumByDouble { it.amount }
  }
}