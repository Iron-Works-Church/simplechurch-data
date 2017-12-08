package org.ironworkschurch.analytics.to

data class Table(
  val tableName: String,
  val columns: List<Column>,
  val constraints: List<Constraint>
) {
  val businessKeys get() = constraints
  .firstOrNull { it.constraintName == "bus_key" }
  ?.columns
    ?.filterNot { keyColumnName -> columns
      .single { it.columnName == keyColumnName }
      .dataType == "datetime"
    }
}

data class Column(
  val tableName: String,
  val columnName: String,
  val dataType: String,
  val isNullable: Boolean
)

data class Constraint(val tableName: String,
                      val constraintName: String,
                      val constraintType: String,
                      val columns: List<String>)