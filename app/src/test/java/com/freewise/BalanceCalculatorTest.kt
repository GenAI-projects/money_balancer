package com.freewise

import com.freewise.data.BalanceCalculator
import com.freewise.data.SampleData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BalanceCalculatorTest {
    @Test
    fun calculateBalances_isNetZero() {
        val group = SampleData.demoGroup()
        val net = BalanceCalculator.calculateBalances(group).values.sum()
        assertTrue(kotlin.math.abs(net) < 0.01)
    }

    @Test
    fun simplifyDebts_includesPaymentLinksAndCurrency() {
        val group = SampleData.demoGroup()
        val settlements = BalanceCalculator.simplifyDebts(group)

        assertTrue(settlements.isNotEmpty())
        assertEquals(group.baseCurrency, settlements.first().currency)
        assertTrue(settlements.first().upiLink.startsWith("upi://pay"))
        assertTrue(settlements.first().paypalLink.contains("paypal.me"))
    }
}
