package com.freewise.data

import com.freewise.model.Expense
import com.freewise.model.ExpenseCategory
import com.freewise.model.Group
import com.freewise.model.Person

object SampleData {
    private val alex = Person("p1", "Alex")
    private val rina = Person("p2", "Rina")
    private val sam = Person("p3", "Sam")
    private val jo = Person("p4", "Jo")

    fun demoGroup(): Group {
        val members = listOf(alex, rina, sam, jo)

        val expenses = listOf(
            Expense(
                id = "e1",
                title = "Groceries",
                amount = 120.0,
                paidBy = alex,
                splitBetween = members,
                category = ExpenseCategory.FOOD,
                note = "Weekly shopping"
            ),
            Expense(
                id = "e2",
                title = "Uber",
                amount = 36.0,
                paidBy = rina,
                splitBetween = listOf(alex, rina, sam),
                category = ExpenseCategory.TRANSPORT
            ),
            Expense(
                id = "e3",
                title = "Rent",
                amount = 800.0,
                paidBy = sam,
                splitBetween = members,
                category = ExpenseCategory.RENT,
                recurringDays = 30
            )
        )

        return Group(
            id = "g1",
            name = "Roommates",
            members = members,
            expenses = expenses
        )
    }
}
