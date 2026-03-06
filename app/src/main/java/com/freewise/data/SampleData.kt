package com.freewise.data

import com.freewise.model.CurrencyCode
import com.freewise.model.Expense
import com.freewise.model.ExpenseCategory
import com.freewise.model.Group
import com.freewise.model.Person
import com.freewise.model.Reminder

object SampleData {
    private val alex = Person("p1", "Alex", upiId = "alex@upi", paypalHandle = "alex")
    private val rina = Person("p2", "Rina", upiId = "rina@upi", paypalHandle = "rina")
    private val sam = Person("p3", "Sam", upiId = "sam@upi", paypalHandle = "sam")
    private val jo = Person("p4", "Jo", upiId = "jo@upi", paypalHandle = "jo")

    fun demoGroup(): Group {
        val members = listOf(alex, rina, sam, jo)

        val expenses = listOf(
            Expense(
                id = "e1",
                title = "Groceries",
                amount = 120.0,
                currency = CurrencyCode.USD,
                paidBy = alex,
                splitBetween = members,
                category = ExpenseCategory.FOOD,
                note = "Weekly shopping",
                receiptPath = "receipt_001.jpg"
            ),
            Expense(
                id = "e2",
                title = "Shibuya Taxi",
                amount = 3600.0,
                currency = CurrencyCode.JPY,
                paidBy = rina,
                splitBetween = listOf(alex, rina, sam),
                category = ExpenseCategory.TRANSPORT
            ),
            Expense(
                id = "e3",
                title = "Rent",
                amount = 800.0,
                currency = CurrencyCode.USD,
                paidBy = sam,
                splitBetween = members,
                category = ExpenseCategory.RENT,
                recurringDays = 30
            )
        )

        val reminders = listOf(
            Reminder("r1", rina, "Settle this week", System.currentTimeMillis() + 86_400_000),
            Reminder("r2", jo, "Rent reminder", System.currentTimeMillis() + 172_800_000, snoozedCount = 1)
        )

        return Group(
            id = "g1",
            name = "Neo-Tokyo Roommates",
            members = members,
            expenses = expenses,
            reminders = reminders,
            baseCurrency = CurrencyCode.USD
        )
    }
}
