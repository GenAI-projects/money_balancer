package com.freewise.model

data class Person(
    val id: String,
    val name: String,
    val upiId: String? = null,
    val paypalHandle: String? = null
)

data class Expense(
    val id: String,
    val title: String,
    val amount: Double,
    val currency: CurrencyCode = CurrencyCode.USD,
    val paidBy: Person,
    val splitBetween: List<Person>,
    val category: ExpenseCategory,
    val note: String = "",
    val recurringDays: Int? = null,
    val receiptPath: String? = null
)

enum class ExpenseCategory {
    FOOD,
    TRANSPORT,
    RENT,
    UTILITIES,
    ENTERTAINMENT,
    OTHER
}

enum class CurrencyCode {
    USD,
    EUR,
    GBP,
    INR,
    JPY
}

data class Reminder(
    val id: String,
    val person: Person,
    val message: String,
    val dueEpochMs: Long,
    val snoozedCount: Int = 0
)

data class Settlement(
    val from: Person,
    val to: Person,
    val amount: Double,
    val currency: CurrencyCode = CurrencyCode.USD,
    val upiLink: String = "",
    val paypalLink: String = ""
)

data class Group(
    val id: String,
    val name: String,
    val members: List<Person>,
    val expenses: List<Expense>,
    val reminders: List<Reminder> = emptyList(),
    val baseCurrency: CurrencyCode = CurrencyCode.USD
)
