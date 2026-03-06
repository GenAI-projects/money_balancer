package com.freewise.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.freewise.data.BalanceCalculator
import com.freewise.data.SampleData
import com.freewise.model.Expense
import com.freewise.model.Group
import com.freewise.model.Settlement

private enum class HomeTab(val label: String) {
    DASHBOARD("Dashboard"),
    EXPENSES("Expenses"),
    SETTLE("Settle"),
    INSIGHTS("Insights")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeWiseApp() {
    val group = remember { SampleData.demoGroup() }
    var selectedTab by remember { mutableStateOf(HomeTab.DASHBOARD) }

    val bladeRunnerBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF07020F), Color(0xFF1B0B33), Color(0xFF0A0618))
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { selectedTab = HomeTab.EXPENSES }) {
                Icon(Icons.Default.Add, contentDescription = "Add expense")
            }
        },
        bottomBar = {
            BottomAppBar {
                HomeTab.entries.forEach { tab ->
                    AssistChip(
                        onClick = { selectedTab = tab },
                        label = { Text(tab.label) },
                        leadingIcon = {
                            val icon = when (tab) {
                                HomeTab.DASHBOARD -> Icons.Default.Group
                                HomeTab.EXPENSES -> Icons.Default.ReceiptLong
                                HomeTab.SETTLE -> Icons.Default.Payments
                                HomeTab.INSIGHTS -> Icons.Default.AutoGraph
                            }
                            Icon(icon, contentDescription = tab.label)
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bladeRunnerBrush)
                .padding(contentPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "FreeWise — Cyber Noir Edition",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF22F0FF),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            when (selectedTab) {
                HomeTab.DASHBOARD -> DashboardTab(group)
                HomeTab.EXPENSES -> ExpensesTab(group.expenses)
                HomeTab.SETTLE -> SettleTab(BalanceCalculator.simplifyDebts(group))
                HomeTab.INSIGHTS -> InsightsTab(group)
            }
        }
    }
}

@Composable
private fun DashboardTab(group: Group) {
    val balances = BalanceCalculator.calculateBalances(group)

    Text("Group: ${group.name}", fontWeight = FontWeight.SemiBold, color = Color.White)
    Text("Base currency: ${group.baseCurrency}", color = Color(0xFFBBA7D8))
    Spacer(modifier = Modifier.height(8.dp))

    balances.forEach { (person, balance) ->
        val label = if (balance >= 0) "gets" else "owes"
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(person.name)
                Text("$label $${"%.2f".format(kotlin.math.abs(balance))}")
            }
        }
    }
}

@Composable
private fun ExpensesTab(expenses: List<Expense>) {
    Text("Expenses", color = Color.White)
    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(expenses) { expense ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(expense.title, fontWeight = FontWeight.SemiBold)
                    Text("Category: ${expense.category}")
                    Text("Paid by: ${expense.paidBy.name}")
                    Text("Amount: ${expense.currency} ${"%.2f".format(expense.amount)}")
                    if (expense.recurringDays != null) Text("Recurring every ${expense.recurringDays} days")
                    if (expense.note.isNotBlank()) Text("Note: ${expense.note}")
                    if (expense.receiptPath != null) Text("OCR receipt: ${expense.receiptPath}")
                }
            }
        }
    }
}

@Composable
private fun SettleTab(settlements: List<Settlement>) {
    Text("Simplified settlements + deep-links", color = Color.White)
    Spacer(modifier = Modifier.height(8.dp))

    if (settlements.isEmpty()) {
        Text("All settled up 🎉", color = Color(0xFFBBA7D8))
    } else {
        settlements.forEach { settlement ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${settlement.from.name} → ${settlement.to.name}")
                        Text("${settlement.currency} ${"%.2f".format(settlement.amount)}")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("UPI: ${settlement.upiLink}", style = MaterialTheme.typography.bodySmall)
                    Text("PayPal: ${settlement.paypalLink}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun InsightsTab(group: Group) {
    Text("Live upgrades integrated", color = Color.White)
    Spacer(modifier = Modifier.height(8.dp))

    val totalSpent = group.expenses.sumOf { it.amount }
    val avgExpense = if (group.expenses.isEmpty()) 0.0 else totalSpent / group.expenses.size
    val recurringCount = group.expenses.count { it.recurringDays != null }
    val foreignCurrencyCount = group.expenses.count { it.currency != group.baseCurrency }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Total tracked (raw): $${"%.2f".format(totalSpent)}")
            Text("Average expense (raw): $${"%.2f".format(avgExpense)}")
            Text("Recurring expenses: $recurringCount")
            Text("Travel mode expenses: $foreignCurrencyCount")
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            Text("Enabled features:")
            Text("• OCR receipt metadata")
            Text("• Payment deep-links in settlements")
            Text("• Reminder + snooze data")
            Text("• Offline-first ready via local source")
            Text("• Multi-currency normalization")
            Text("• Export-ready structure for CSV/PDF")
            Text("Active reminders: ${group.reminders.size}")
        }
    }
}
