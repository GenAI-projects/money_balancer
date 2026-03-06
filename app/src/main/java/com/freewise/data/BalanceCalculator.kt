package com.freewise.data

import com.freewise.model.Group
import com.freewise.model.Person
import com.freewise.model.Settlement
import kotlin.math.abs

object BalanceCalculator {
    fun calculateBalances(group: Group): Map<Person, Double> {
        val balances = group.members.associateWith { 0.0 }.toMutableMap()

        group.expenses.forEach { expense ->
            val splitAmount = expense.amount / expense.splitBetween.size

            balances[expense.paidBy] = balances.getValue(expense.paidBy) + expense.amount
            expense.splitBetween.forEach { member ->
                balances[member] = balances.getValue(member) - splitAmount
            }
        }

        return balances
    }

    fun simplifyDebts(group: Group): List<Settlement> {
        val balances = calculateBalances(group)
        val debtors = mutableListOf<Pair<Person, Double>>()
        val creditors = mutableListOf<Pair<Person, Double>>()

        balances.forEach { (person, balance) ->
            when {
                balance < -0.01 -> debtors.add(person to -balance)
                balance > 0.01 -> creditors.add(person to balance)
            }
        }

        debtors.sortByDescending { it.second }
        creditors.sortByDescending { it.second }

        val settlements = mutableListOf<Settlement>()
        var dIndex = 0
        var cIndex = 0

        while (dIndex < debtors.size && cIndex < creditors.size) {
            val (debtor, owes) = debtors[dIndex]
            val (creditor, gets) = creditors[cIndex]
            val transfer = minOf(owes, gets)

            settlements += Settlement(
                from = debtor,
                to = creditor,
                amount = (transfer * 100).toInt() / 100.0
            )

            val remainingDebt = owes - transfer
            val remainingCredit = gets - transfer

            if (abs(remainingDebt) < 0.01) {
                dIndex++
            } else {
                debtors[dIndex] = debtor to remainingDebt
            }

            if (abs(remainingCredit) < 0.01) {
                cIndex++
            } else {
                creditors[cIndex] = creditor to remainingCredit
            }
        }

        return settlements
    }
}
