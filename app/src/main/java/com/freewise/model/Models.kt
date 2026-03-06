package com.freewise.model

data class Person(
    val id: String,
    val name: String
)

data class Expense(
    val id: String,
    val title: String,
    val amount: Double,
    val paidBy: Person,
    val splitBetween: List<Person>,
    val category: ExpenseCategory,
    val note: String = "",
    val recurringDays: Int? = null
)

enum class ExpenseCategory {
    FOOD,
    TRANSPORT,
    RENT,
    UTILITIES,
    ENTERTAINMENT,
    OTHER
}

data class Settlement(
    val from: Person,
    val to: Person,
    val amount: Double
)

data class Group(
    val id: String,
    val name: String,
    val members: List<Person>,
    val expenses: List<Expense>
)
